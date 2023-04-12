package tools.kot.nk2.cdprshop.domain.game.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import tools.kot.nk2.cdprshop.domain.common.protocol.CommonSecurityWebFilterFactory;
import tools.kot.nk2.cdprshop.domain.common.utils.ErrorDetailsResponse;
import tools.kot.nk2.cdprshop.domain.common.utils.SuccessDetailsResponse;
import tools.kot.nk2.cdprshop.domain.game.protocol.GameService;
import tools.kot.nk2.cdprshop.domain.user.protocol.User;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class GameDomainRouterConfiguration {
    
    private final GameService gameService;

    private final CommonSecurityWebFilterFactory securityWebFilterFactory;


    @Autowired
    public GameDomainRouterConfiguration(GameService gameService, CommonSecurityWebFilterFactory securityWebFilterFactory) {
        this.gameService = gameService;
        this.securityWebFilterFactory = securityWebFilterFactory;
    }
    
    @Bean("gameRouter")
    public RouterFunction<ServerResponse> gameRouter() {
        var securityFilter = securityWebFilterFactory
            .create(List.of(
                new CommonSecurityWebFilterFactory.FilterConfiguration(
                    "/api/game",
                    new CommonSecurityWebFilterFactory.SomeMethods(List.of(HttpMethod.POST)),
                    new CommonSecurityWebFilterFactory.SomeRoles(List.of(User.UserRole.SYSTEM, User.UserRole.ADMIN))
                ),
                new CommonSecurityWebFilterFactory.FilterConfiguration(
                    "/api/game/{id}",
                    new CommonSecurityWebFilterFactory.SomeMethods(List.of(HttpMethod.PUT, HttpMethod.DELETE)),
                    new CommonSecurityWebFilterFactory.SomeRoles(List.of(User.UserRole.SYSTEM, User.UserRole.ADMIN))
                )
            ));

        var gameIdGetRoute = route(
            GET("/api/game/{id}"),
            request -> gameService
                .findGameById(Long.parseLong(request.pathVariable("id")))
                .flatMap((result) -> switch (result) {
                    case GameService.OkGameByIdFindResult okGameByIdFindResult -> ServerResponse
                        .ok()
                        .bodyValue(okGameByIdFindResult.game());
                    case GameService.NotFoundGameByIdFindResult notFoundGameByIdFindResult -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.NOT_FOUND,
                            "Game not found by id."
                        ));
                })
        );

        var gamePostRoute = route(
            POST("/api/game"),
            request -> request
                .bodyToMono(GameService.GameCreateRequest.class)
                .flatMap(gameService::createGame)
                .flatMap((result) -> switch (result) {
                    case GameService.OkGameCreateResult okGamesSaveResult -> ServerResponse
                        .ok()
                        .bodyValue(okGamesSaveResult.game());
                    case GameService.TagsNotFoundFoundGameCreateResult duplicatesFoundGamesSaveResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Tags provided cannot be fully found."
                        ));
                    case GameService.TitleOrDescriptionEmptyGameCreateResult valueEmptyGameCreateResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Game must not have empty title or description."
                        ));
                })
        );

        var gameIdPutRoute = route(
            PUT("/api/game/{id}"),
            request -> request
                .bodyToMono(GameService.GameByIdUpdateRequest.class)
                .flatMap((game) -> gameService
                    .updateGameById(
                        game.withId(Long.parseLong(request.pathVariable("id")))
                    )
                )
                .flatMap((result) -> switch (result) {
                    case GameService.OkGameByIdUpdateResult okGameByIdUpdateResult -> ServerResponse
                        .ok()
                        .bodyValue(okGameByIdUpdateResult.game());
                    case GameService.NotFoundGameByIdUpdateResult notFoundGameByIdUpdateResult -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.NOT_FOUND,
                            "Game not found."
                        ));
                    case GameService.TagsNotFoundGameByIdUpdateResult tagsNotFoundGameByIdUpdateResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Tags provided cannot be fully found."
                        ));
                    case GameService.TitleOrDescriptionEmptyGameByIdUpdateResult titleOrDescriptionEmptyGameByIdUpdateResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Game must not have empty title or description."
                        ));
                })
        );

        var gameIdDeleteRoute = route(
            DELETE("/api/game/{id}"),
            request -> gameService
                .deleteGameById(Long.parseLong(request.pathVariable("id")))
                .flatMap((result) -> switch (result) {
                    case GameService.OkGameByIdDeleteResult okGameByIdDeleteResult -> ServerResponse
                        .ok()
                        .bodyValue(new SuccessDetailsResponse(
                            HttpStatus.OK,
                            "Game deleted."
                        ));
                    case GameService.NotFoundGameByIdDeleteResult notFoundGameByIdDeleteResult -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.NOT_FOUND,
                            "Game not found by id."
                        ));
                })
        );
        
        return gameIdGetRoute.filter(securityFilter)
            .and(gamePostRoute.filter(securityFilter))
            .and(gameIdPutRoute.filter(securityFilter))
            .and(gameIdDeleteRoute.filter(securityFilter));
    }
}
