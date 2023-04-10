package tools.kot.nk2.cdprshop.domain.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.PathMatcher;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tools.kot.nk2.cdprshop.domain.common.protocol.CommonSecurityWebFilterFactory;
import tools.kot.nk2.cdprshop.domain.common.utils.ErrorDetailsResponse;
import tools.kot.nk2.cdprshop.domain.user.protocol.User;
import tools.kot.nk2.cdprshop.domain.user.protocol.UserService;

import java.util.Collections;
import java.util.List;

@Service
public class CommonSecurityWebFilterFactoryImpl implements CommonSecurityWebFilterFactory {

    private final PathMatcher pathMatcher;

    private final UserService userService;

    @Autowired
    public CommonSecurityWebFilterFactoryImpl(
        PathMatcher pathMatcher,
        UserService userService
    ) {
        this.pathMatcher = pathMatcher;
        this.userService = userService;
    }

    @Override
    public HandlerFilterFunction<ServerResponse, ServerResponse> create(List<FilterConfiguration> configurations) {
        return (request, next) -> {
            var matchedConfiguration = configurations
                .stream()
                .filter((configuration) -> pathMatcher.match(configuration.pathPattern(), request.path()))
                .findFirst();

            var hasMatchByPath = matchedConfiguration.isPresent();

            var hasMatchByMethod = matchedConfiguration
                .map((configuration) -> request.method() != HttpMethod.OPTIONS
                    && switch (configuration.methodMode()) {
                        case AllMethods allMethods -> true;
                        case SomeMethods someMethods -> someMethods.methods().contains(request.method());
                    }
                )
                .orElse(false);

            if (!hasMatchByPath || !hasMatchByMethod) {
                return next.handle(request);
            }

            var token = request
                .headers()
                .header("Authorization")
                .stream()
                .findFirst();

            return Mono
                .justOrEmpty(token)
                .map((value) -> value.replaceFirst("^Bearer", ""))
                .flatMap(userService::validateCredentials)
                .flatMap((result) -> switch (result) {
                    case UserService.OkCredentialsValidateResult okCredentialsValidateResult -> {
                        if(!roleIsAllowed(
                            matchedConfiguration.get().allowedRoles(),
                            okCredentialsValidateResult.user().role()
                        )) {
                            yield ServerResponse
                                .status(HttpStatus.FORBIDDEN)
                                .bodyValue(new ErrorDetailsResponse(
                                    HttpStatus.FORBIDDEN,
                                    "User is not authorized to use this resource."
                                ));
                        }

                        request
                            .attributes()
                            .put("currentUser", okCredentialsValidateResult.user());

                        yield next.handle(request);
                    }
                    case UserService.OkRefreshTokenCredentialsValidateResult okRefreshTokenCredentialsValidateResult -> ServerResponse
                        .status(HttpStatus.UNAUTHORIZED)
                        .headers((headers) -> headers.put("X-Refresh-Token", Collections.singletonList(okRefreshTokenCredentialsValidateResult.token())))
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.UNAUTHORIZED,
                            "Credentials are expired."
                        ));
                    case UserService.InvalidCredentialsValidateResult invalidCredentialsValidateResult -> ServerResponse
                        .status(HttpStatus.UNAUTHORIZED)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.UNAUTHORIZED,
                            "Credentials are invalid."
                        ));
                });
        };
    }

    private Boolean roleIsAllowed(RoleMode roles, User.UserRole role) {
        return switch (roles) {
            case AllRoles allRoles -> true;
            case SomeRoles someRoles -> someRoles.roles().contains(role);
        };
    }
}
