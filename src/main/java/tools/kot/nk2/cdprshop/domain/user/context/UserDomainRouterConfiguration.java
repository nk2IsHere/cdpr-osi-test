package tools.kot.nk2.cdprshop.domain.user.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.info.Info;
import lombok.NonNull;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tools.kot.nk2.cdprshop.domain.common.protocol.CommonSecurityWebFilterFactory;
import tools.kot.nk2.cdprshop.domain.common.utils.ErrorDetailsResponse;
import tools.kot.nk2.cdprshop.domain.common.utils.SuccessDetailsResponse;
import tools.kot.nk2.cdprshop.domain.common.utils.WebUtils;
import tools.kot.nk2.cdprshop.domain.user.protocol.User;
import tools.kot.nk2.cdprshop.domain.user.protocol.UserService;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserDomainRouterConfiguration {
    
    private final UserService userService;

    private final CommonSecurityWebFilterFactory securityWebFilterFactory;

    @Autowired
    public UserDomainRouterConfiguration(
        UserService userService,
        CommonSecurityWebFilterFactory securityWebFilterFactory
    ) {
        this.userService = userService;
        this.securityWebFilterFactory = securityWebFilterFactory;
    }

    @Bean
    public GroupedOpenApi userOpenApi() {
        return GroupedOpenApi
            .builder()
            .group("user")
            .addOpenApiCustomizer(openApi -> openApi
                .info(
                    new Info()
                        .title("User Domain API")
                )
            )
            .pathsToMatch("/api/user/**")
            .build();
    }

    @Bean("userRouter")
    @RouterOperations({
        @RouterOperation(
            path = "/api/user/authenticate",
            method = RequestMethod.POST,
            beanClass = UserService.class,
            beanMethod = "generateCredentials",
            operation = @Operation(
                summary = "Authenticate user",
                responses = {
                    @ApiResponse(responseCode = "200", description = "Successful authentication", content = @Content(schema = @Schema(implementation = UserService.NewCredentialsGenerateResult.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ErrorDetailsResponse.class)))
                }
            )
        ),
        @RouterOperation(
            path = "/api/user",
            method = RequestMethod.GET,
            beanClass = UserService.class,
            beanMethod = "getUserById",
            operation = @Operation(
                summary = "Get user by ID",
                responses = {
                    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorDetailsResponse.class)))
                }
            )
        ),
        @RouterOperation(
            path = "/api/user",
            method = RequestMethod.PUT,
            beanClass = UserService.class,
            beanMethod = "updateUserInformationById",
            operation = @Operation(
                summary = "Update user information",
                responses = {
                    @ApiResponse(responseCode = "200", description = "User information updated", content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Duplicate username", content = @Content(schema = @Schema(implementation = ErrorDetailsResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorDetailsResponse.class)))
                }
            )
        ),
        @RouterOperation(
            path = "/api/user",
            method = RequestMethod.POST,
            beanClass = UserService.class,
            beanMethod = "createUser",
            operation = @Operation(
                summary = "Create user",
                responses = {
                    @ApiResponse(responseCode = "200", description = "User created", content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Duplicate username", content = @Content(schema = @Schema(implementation = ErrorDetailsResponse.class)))
                }
            )
        ),
        @RouterOperation(
            path = "/api/user/credentials",
            method = RequestMethod.PUT,
            beanClass = UserService.class,
            beanMethod = "updateUserCredentialsById",
            operation = @Operation(
                summary = "Update user credentials",
                responses = {
                    @ApiResponse(responseCode = "200", description = "User credentials updated", content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Old password mismatch", content = @Content(schema = @Schema(implementation = ErrorDetailsResponse.class)))
                }
            )
        ),
        @RouterOperation(
            path = "/api/user/{id}",
            method = RequestMethod.DELETE,
            beanClass = UserService.class,
            beanMethod = "deleteUserById",
            operation = @Operation(
                summary = "Delete user by ID",
                responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted", content = @Content(schema = @Schema(implementation = SuccessDetailsResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorDetailsResponse.class)))
                }
            )
        )
    })
    public RouterFunction<ServerResponse> userRouter() {
        var securityFilter = securityWebFilterFactory
            .create(List.of(
                new CommonSecurityWebFilterFactory.FilterConfiguration(
                    "/api/user",
                    new CommonSecurityWebFilterFactory.SomeMethods(List.of(HttpMethod.GET, HttpMethod.PUT)),
                    new CommonSecurityWebFilterFactory.AllRoles()
                ),
                new CommonSecurityWebFilterFactory.FilterConfiguration(
                    "/api/user",
                    new CommonSecurityWebFilterFactory.SomeMethods(List.of(HttpMethod.POST)),
                    new CommonSecurityWebFilterFactory.SomeRoles(List.of(User.UserRole.SYSTEM, User.UserRole.ADMIN))
                ),
                new CommonSecurityWebFilterFactory.FilterConfiguration(
                    "/api/user",
                    new CommonSecurityWebFilterFactory.SomeMethods(List.of(HttpMethod.DELETE)),
                    new CommonSecurityWebFilterFactory.SomeRoles(List.of(User.UserRole.SYSTEM))
                ),
                new CommonSecurityWebFilterFactory.FilterConfiguration(
                    "/api/user/credentials",
                    new CommonSecurityWebFilterFactory.SomeMethods(List.of(HttpMethod.PUT)),
                    new CommonSecurityWebFilterFactory.AllRoles()
                )
            ));

        var userAuthenticatePostRoute = route(
            POST("/api/user/authenticate"),
            request -> request
                .bodyToMono(UserAuthenticateRequest.class)
                .flatMap((body) -> userService.generateCredentials(body.username(), body.password()))
                .flatMap((result) -> switch(result) {
                    case UserService.NewCredentialsGenerateResult newCredentialsGenerateResult -> ServerResponse
                        .ok()
                        .bodyValue(newCredentialsGenerateResult);
                    case UserService.InvalidCredentialsGenerateResult invalidCredentialsGenerateResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Provided credentials are invalid."
                        ));
                })
        );

        var userGetRoute = route(
            GET("/api/user"),
            request -> Mono
                .justOrEmpty(WebUtils.getCurrentUser(request))
                .flatMap((currentUser) -> userService.getUserById(currentUser.id()))
                .flatMap((result) -> switch (result) {
                    case UserService.OkUserByIdGetResult okUserByIdGetResult -> ServerResponse
                        .ok()
                        .bodyValue(okUserByIdGetResult.user());
                    case UserService.NotFoundUserByIdGetResult notFoundUserByIdGetResult -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.NOT_FOUND,
                            "Current user not found in database."
                        ));
                })
        );

        var userPutRoute = route(
            PUT("/api/user"),
            request -> Mono
                .justOrEmpty(WebUtils.getCurrentUser(request))
                .zipWith(request.bodyToMono(UserService.UserInformationByIdUpdateRequest.class))
                .flatMap((currentUserToRequest) -> userService.updateUserInformationById(
                    currentUserToRequest.getT2()
                        .withId(currentUserToRequest.getT1().id())
                ))
                .flatMap((result) -> switch(result) {
                    case UserService.OkUserInformationByIdUpdateResult okUserInformationByIdUpdateResult -> ServerResponse
                        .ok()
                        .bodyValue(okUserInformationByIdUpdateResult.user());
                    case UserService.DuplicateUsernameUserInformationByIdUpdateResult duplicateUsernameUserInformationByIdUpdateResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Username is duplicated."
                        ));
                    case UserService.NotFoundUserInformationByIdUpdateResult notFoundUserInformationByIdUpdateResult -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.NOT_FOUND,
                            "Current user not found in database."
                        ));
                })
        );

        var userPostRoute = route(
            POST("/api/user"),
            request -> request
                .bodyToMono(UserService.UserCreateRequest.class)
                .flatMap(userService::createUser)
                .flatMap((result) -> switch (result) {
                    case UserService.OkUserCreateResult okUserCreateResult -> ServerResponse
                        .ok()
                        .bodyValue(okUserCreateResult.user());
                    case UserService.DuplicateUsernameUserCreateResult duplicateUsernameUserCreateResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Username is duplicated."
                        ));
                })
        );

        var userCredentialsPutRoute = route(
            PUT("/api/user/credentials"),
            request -> Mono
                .justOrEmpty(WebUtils.getCurrentUser(request))
                .zipWith(request.bodyToMono(UserService.UserCredentialsByIdUpdateRequest.class))
                .flatMap((currentUserToRequest) -> userService.updateUserCredentialsById(
                    currentUserToRequest.getT2()
                        .withId(currentUserToRequest.getT1().id())
                ))
                .flatMap((result) -> switch (result) {
                    case UserService.OkUserCredentialsByIdUpdateResult okUserCredentialsByIdUpdateResult -> ServerResponse
                        .ok()
                        .bodyValue(okUserCredentialsByIdUpdateResult.user());
                    case UserService.NotFoundUserCredentialsByIdUpdateResult notFoundUserCredentialsByIdUpdateResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Old password mismatch."
                        ));
                })
        );

        var userIdDeleteRoute = route(
            DELETE("/api/user/{id}"),
            request -> userService
                .deleteUserById(Long.parseLong(request.pathVariable("id")))
                .flatMap((result) -> switch (result) {
                    case UserService.OkUserByIdDeleteResult okUserByIdDeleteResult -> ServerResponse
                        .ok()
                        .bodyValue(new SuccessDetailsResponse(
                            HttpStatus.OK,
                            "User deleted."
                        ));
                    case UserService.NotFoundUserByIdDeleteResult notFoundUserByIdDeleteResult -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.NOT_FOUND,
                            "User not found by id."
                        ));
                })
        );

        return userAuthenticatePostRoute.filter(securityFilter)
            .and(userGetRoute.filter(securityFilter))
            .and(userPutRoute.filter(securityFilter))
            .and(userPostRoute.filter(securityFilter))
            .and(userCredentialsPutRoute.filter(securityFilter))
            .and(userIdDeleteRoute.filter(securityFilter));
    }

    record UserAuthenticateRequest(
        @NonNull String username,
        @NonNull String password
    ) {
    }
}
