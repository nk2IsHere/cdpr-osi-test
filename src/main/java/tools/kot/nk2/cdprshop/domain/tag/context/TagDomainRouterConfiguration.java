package tools.kot.nk2.cdprshop.domain.tag.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import tools.kot.nk2.cdprshop.domain.common.protocol.CommonSecurityWebFilterFactory;
import tools.kot.nk2.cdprshop.domain.common.utils.ErrorDetailsResponse;
import tools.kot.nk2.cdprshop.domain.common.utils.SuccessDetailsResponse;
import tools.kot.nk2.cdprshop.domain.tag.protocol.TagService;
import tools.kot.nk2.cdprshop.domain.user.protocol.User;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TagDomainRouterConfiguration {

    private final TagService tagService;

    private final CommonSecurityWebFilterFactory securityWebFilterFactory;

    @Autowired
    public TagDomainRouterConfiguration(
        TagService tagService,
        CommonSecurityWebFilterFactory securityWebFilterFactory
    ) {
        this.tagService = tagService;
        this.securityWebFilterFactory = securityWebFilterFactory;
    }

    @Bean("tagRouter")
    public RouterFunction<ServerResponse> tagRouter() {
        var securityFilter = securityWebFilterFactory
            .create(List.of(
                new CommonSecurityWebFilterFactory.FilterConfiguration(
                    "/api/tag",
                    new CommonSecurityWebFilterFactory.SomeMethods(List.of(HttpMethod.POST)),
                    new CommonSecurityWebFilterFactory.SomeRoles(List.of(User.UserRole.SYSTEM, User.UserRole.ADMIN))
                ),
                new CommonSecurityWebFilterFactory.FilterConfiguration(
                    "/api/tag/{id}",
                    new CommonSecurityWebFilterFactory.SomeMethods(List.of(HttpMethod.DELETE)),
                    new CommonSecurityWebFilterFactory.SomeRoles(List.of(User.UserRole.SYSTEM, User.UserRole.ADMIN))
                )
            ));

        var tagGetRoute = route(
            GET("/api/tag"),
            request -> tagService
                .findAllTags(
                    Pageable
                        .ofSize(
                            request
                                .queryParam("size")
                                .map(Integer::parseInt)
                                .orElse(50)
                        )
                        .withPage(
                            request
                                .queryParam("page")
                                .map(Integer::parseInt)
                                .orElse(0)
                        )
                )
                .flatMap((result) -> switch (result) {
                    case TagService.OkAllTagsFindResult okAllTagsFindResult -> ServerResponse
                        .ok()
                        .bodyValue(okAllTagsFindResult.tags());
                })
        );

        var tagIdGetRoute = route(
            GET("/api/tag/{id}"),
            request -> tagService
                .findTagById(Long.parseLong(request.pathVariable("id")))
                .flatMap((result) -> switch (result) {
                    case TagService.OkTagByIdFindResult okTagByIdFindResult -> ServerResponse
                        .ok()
                        .bodyValue(okTagByIdFindResult.tag());
                    case TagService.NotFoundTagByIdFindResult notFoundTagByIdFindResult -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.NOT_FOUND,
                            "Tag not found by id."
                        ));
                })
        );

        var tagPostRoute = route(
            POST("/api/tag"),
            request -> request
                .bodyToMono(TagService.TagCreateRequest.class)
                .flatMap(tagService::createTag)
                .flatMap((result) -> switch (result) {
                    case TagService.OkTagCreateResult okTagsSaveResult -> ServerResponse
                        .ok()
                        .bodyValue(okTagsSaveResult.tag());
                    case TagService.DuplicateTagTagCreateResult duplicatesFoundTagsSaveResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Such tag already exists."
                        ));
                    case TagService.ValueEmptyTagCreateResult valueEmptyTagCreateResult -> ServerResponse
                        .badRequest()
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.BAD_REQUEST,
                            "Tag must not have empty value."
                        ));
                })
        );

        var tagIdDeleteRoute = route(
            DELETE("/api/tag/{id}"),
            request -> tagService
                .deleteTagById(Long.parseLong(request.pathVariable("id")))
                .flatMap((result) -> switch (result) {
                    case TagService.OkTagByIdDeleteResult okTagByIdDeleteResult -> ServerResponse
                        .ok()
                        .bodyValue(new SuccessDetailsResponse(
                            HttpStatus.OK,
                            "Tag deleted."
                        ));
                    case TagService.NotFoundTagByIdDeleteResult notFoundTagByIdDeleteResult -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(new ErrorDetailsResponse(
                            HttpStatus.NOT_FOUND,
                            "Tag not found by id."
                        ));
                })
        );

        return tagGetRoute.filter(securityFilter)
            .and(tagIdGetRoute.filter(securityFilter))
            .and(tagPostRoute.filter(securityFilter))
            .and(tagIdDeleteRoute.filter(securityFilter));
    }
}
