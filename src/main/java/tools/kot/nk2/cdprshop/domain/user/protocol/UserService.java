package tools.kot.nk2.cdprshop.domain.user.protocol;

import lombok.NonNull;
import lombok.With;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<CredentialsGenerateResult> generateCredentials(@NonNull final String username, @NonNull final String password);

    sealed interface CredentialsGenerateResult {
    }

    record NewCredentialsGenerateResult(
        @NonNull String token
    ) implements CredentialsGenerateResult {
    }

    record InvalidCredentialsGenerateResult(
    ) implements CredentialsGenerateResult {
    }

    Mono<CredentialsValidateResult> validateCredentials(@NonNull final String token);

    sealed interface CredentialsValidateResult {
    }

    record OkCredentialsValidateResult(
        @NonNull User user
    ) implements CredentialsValidateResult {
    }

    record OkRefreshTokenCredentialsValidateResult(
        @NonNull User user,
        @NonNull String token
    ) implements CredentialsValidateResult {
    }

    record InvalidCredentialsValidateResult(
    ) implements CredentialsValidateResult {
    }

    Mono<UserCreateResult> createUser(UserCreateRequest request);

    @With
    record UserCreateRequest(
        @NonNull String username,
        @NonNull String password,
        @NonNull User.UserRole role
    ) {
    }

    sealed interface UserCreateResult {
    }

    record OkUserCreateResult(
        @NonNull User user
    ) implements UserCreateResult {
    }

    record DuplicateUsernameUserCreateResult(
    ) implements UserCreateResult {
    }

    Mono<UserByIdGetResult> getUserById(Long id);

    sealed interface UserByIdGetResult {
    }

    record OkUserByIdGetResult(
        @NonNull User user
    ) implements UserByIdGetResult {
    }

    record NotFoundUserByIdGetResult(
    ) implements UserByIdGetResult {
    }

    Mono<UserInformationByIdUpdateResult> updateUserInformationById(UserInformationByIdUpdateRequest request);

    @With
    record UserInformationByIdUpdateRequest(
        Long id,
        String username
    ) {
    }

    sealed interface UserInformationByIdUpdateResult {
    }

    record OkUserInformationByIdUpdateResult(
        @NonNull User user
    ) implements UserInformationByIdUpdateResult {
    }

    record NotFoundUserInformationByIdUpdateResult(
    ) implements UserInformationByIdUpdateResult {
    }

    record DuplicateUsernameUserInformationByIdUpdateResult(
    ) implements UserInformationByIdUpdateResult {
    }

    Mono<UserCredentialsByIdUpdateResult> updateUserCredentialsById(UserCredentialsByIdUpdateRequest request);

    @With
    record UserCredentialsByIdUpdateRequest(
        Long id,
        @NonNull String oldPassword,
        @NonNull String newPassword
    ) {
    }

    sealed interface UserCredentialsByIdUpdateResult {
    }

    record OkUserCredentialsByIdUpdateResult(
        @NonNull User user
    ) implements UserCredentialsByIdUpdateResult {
    }

    record NotFoundUserCredentialsByIdUpdateResult(
    ) implements UserCredentialsByIdUpdateResult {
    }

    Mono<UserByIdDeleteResult> deleteUserById(Long id);

    sealed interface UserByIdDeleteResult {
    }

    record OkUserByIdDeleteResult(
    ) implements UserByIdDeleteResult {
    }

    record NotFoundUserByIdDeleteResult(
    ) implements UserByIdDeleteResult {
    }
}
