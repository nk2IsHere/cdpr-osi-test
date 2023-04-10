package tools.kot.nk2.cdprshop.domain.user;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import tools.kot.nk2.cdprshop.domain.common.utils.ReactorUtils;
import tools.kot.nk2.cdprshop.domain.token.protocol.TokenService;
import tools.kot.nk2.cdprshop.domain.user.protocol.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    @Autowired
    public UserServiceImpl(
        UserRepository repository,
        PasswordEncoder encoder,
        TokenService tokenService
    ) {
        this.repository = repository;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    @Override
    public Mono<CredentialsGenerateResult> generateCredentials(@NonNull String username, @NonNull String password) {
        return repository
            .findByUsername(username)
            .filterWhen((user) ->
                ReactorUtils.async(() ->
                    encoder.matches(password, user.getPassword())
                )
            )
            .flatMap((user) ->
                tokenService.generateToken(user.getUsername())
            )
            .map((result) -> switch (result) {
                case TokenService.OkTokenGenerateResult ok -> new NewCredentialsGenerateResult(ok.data());
            })
            .cast(CredentialsGenerateResult.class)
            .defaultIfEmpty(new InvalidCredentialsGenerateResult());
    }

    @Override
    public Mono<CredentialsValidateResult> validateCredentials(@NonNull String token) {
        return tokenService
            .parseToken(token)
            .flatMap((result) -> switch (result) {
                case TokenService.ValidTokenParseResult valid -> repository
                    .findByUsername(valid.data())
                    .map((user) -> new OkCredentialsValidateResult(
                        user.toResource()
                    ));

                case TokenService.ExpiredTokenParseResult expired -> Mono
                    .zip(
                        repository.findByUsername(expired.data()),
                        tokenService.generateToken(expired.data())
                    )
                    .map((userTokenTuple) -> switch (userTokenTuple.getT2()) {
                        case TokenService.OkTokenGenerateResult ok -> new OkRefreshTokenCredentialsValidateResult(
                            userTokenTuple.getT1().toResource(),
                            ok.data()
                        );
                    });

                case TokenService.InvalidTokenParseResult ignored -> Mono
                    .just(new InvalidCredentialsValidateResult());
            });
    }

    @Override
    public Mono<UserCreateResult> createUser(UserCreateRequest request) {
        return repository
            .existsByUsername(request.username())
            .filter((exists) -> !exists)
            .flatMap((exists) ->
                ReactorUtils.async(() ->
                    new UserEntity()
                        .setUsername(request.username())
                        .setPassword(encoder.encode(request.password()))
                        .setRole(request.role())
                )
            )
            .flatMap(repository::save)
            .map((user) -> new OkUserCreateResult(user.toResource()))
            .cast(UserCreateResult.class)
            .defaultIfEmpty(new DuplicateUsernameUserCreateResult());
    }

    @Override
    public Mono<UserByIdGetResult> getUserById(Long id) {
        return repository
            .findById(id)
            .map((user) -> new OkUserByIdGetResult(
                user.toResource()
            ))
            .cast(UserByIdGetResult.class)
            .defaultIfEmpty(new NotFoundUserByIdGetResult());
    }

    @Override
    public Mono<UserInformationByIdUpdateResult> updateUserInformationById(UserInformationByIdUpdateRequest request) {
        return repository
            .existsByUsername(request.username())
            .filter((exists) -> !exists)
            .flatMap((exists) ->
                repository.findById(request.id())
                    .map((user) -> user
                        .setUsername(
                            request.username() != null
                                ? request.username()
                                : user.getUsername()
                        )
                    )
                    .flatMap(repository::save)
                    .map((user) -> new OkUserInformationByIdUpdateResult(
                        user.toResource()
                    ))
                    .cast(UserInformationByIdUpdateResult.class)
                    .defaultIfEmpty(new NotFoundUserInformationByIdUpdateResult())
            )
            .defaultIfEmpty(new DuplicateUsernameUserInformationByIdUpdateResult());
    }

    @Override
    public Mono<UserCredentialsByIdUpdateResult> updateUserCredentialsById(UserCredentialsByIdUpdateRequest request) {
        return repository
            .findById(request.id())
            .zipWhen((user) ->
                ReactorUtils.async(() ->
                    encoder.matches(request.oldPassword(), user.getPassword())
                )
            )
            .filter(Tuple2::getT2)
            .flatMap((userToPasswordMatches) ->
                ReactorUtils.async(() ->
                    userToPasswordMatches
                        .getT1()
                        .setPassword(encoder.encode(request.newPassword()))
                )
            )
            .flatMap(repository::save)
            .map((user) -> new OkUserCredentialsByIdUpdateResult(
                user.toResource()
            ))
            .cast(UserCredentialsByIdUpdateResult.class)
            .defaultIfEmpty(new NotFoundUserCredentialsByIdUpdateResult());
    }
}
