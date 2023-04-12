package tools.kot.nk2.cdprshop.domain.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.kot.nk2.cdprshop.domain.common.utils.ReactorUtils;
import tools.kot.nk2.cdprshop.domain.common.utils.StreamUtils;
import tools.kot.nk2.cdprshop.domain.game.protocol.Game;
import tools.kot.nk2.cdprshop.domain.game.protocol.GameService;
import tools.kot.nk2.cdprshop.domain.tag.protocol.TagService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository repository;

    private final TagService tagService;

    @Autowired
    public GameServiceImpl(GameRepository repository, TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Override
    public Mono<GameByIdFindResult> findGameById(Long id) {
        return repository
            .findById(id)
            .map((game) -> new OkGameByIdFindResult(game.toResource()))
            .cast(GameByIdFindResult.class)
            .defaultIfEmpty(new NotFoundGameByIdFindResult());
    }

    @Override
    public Mono<GameCreateResult> createGame(GameCreateRequest request) {
        return tagService
            .findTagsByIds(request.tags())
            .filter((result) -> result instanceof TagService.OkTagsByIdsFindResult)
            .cast(TagService.OkTagsByIdsFindResult.class)
            .flatMap((result) -> Mono
                .just(request.description().isEmpty() || request.title().isEmpty())
                .filter((empty) -> !empty)
                .flatMap((empty) -> repository
                    .save(new GameEntity()
                        .setTitle(request.title())
                        .setDescription(request.description())
                        .setPrice(request.price())
                        .setTags(result.tags())
                    )
                )
                .map((game) -> new OkGameCreateResult(game.toResource()))
                .cast(GameCreateResult.class)
                .defaultIfEmpty(new TitleOrDescriptionEmptyGameCreateResult())
            )
            .defaultIfEmpty(new TagsNotFoundFoundGameCreateResult());
    }

    @Override
    public Mono<GameByIdDeleteResult> deleteGameById(Long id) {
        return repository
            .existsById(id)
            .filter((exists) -> exists)
            .flatMap((exists) -> repository.deleteById(id))
            .thenReturn(new OkGameByIdDeleteResult())
            .cast(GameByIdDeleteResult.class)
            .defaultIfEmpty(new NotFoundGameByIdDeleteResult());
    }

    @Override
    public Mono<GameByIdUpdateResult> updateGameById(GameByIdUpdateRequest request) {
        return repository
            .findById(request.id())
            .flatMap((game) -> (request.tags() != null
                ? tagService.findTagsByIds(request.tags())
                : Mono.just(new TagService.OkTagsByIdsFindResult(List.of())))
                .filter((result) -> result instanceof TagService.OkTagsByIdsFindResult)
                .cast(TagService.OkTagsByIdsFindResult.class)
                .flatMap((tags) -> Mono
                    .just(
                        request.title() != null && request.title().isEmpty()
                            || request.description() != null && request.description().isEmpty()
                    )
                    .filter((empty) -> !empty)
                    .flatMap((empty) -> repository
                        .save(
                            game
                                .setTags(request.tags() != null ? tags.tags() : game.getTags())
                                .setTitle(request.title() != null ? request.title() : game.getTitle())
                                .setDescription(request.description() != null ? request.description() : game.getDescription())
                                .setPrice(request.price() != null ? request.price() : game.getPrice())
                        )
                    )
                    .map((result) -> new OkGameByIdUpdateResult(result.toResource()))
                    .cast(GameByIdUpdateResult.class)
                )
                .defaultIfEmpty(new TagsNotFoundGameByIdUpdateResult())
            )
            .defaultIfEmpty(new NotFoundGameByIdUpdateResult());
    }

    @Override
    public Mono<GamesSearchResult> searchGames(GamesSearchRequest request) {
        return Flux
            .concat(
                request.title() != null
                    ? repository.searchByTitle(request.title())
                    : Flux.fromIterable(List.of()),
                request.description() != null
                    ? repository.searchByDescription(request.description())
                    : Flux.fromIterable(List.of())
            )
            .map(GameEntity::toResource)
            .collectList()
            .flatMap((results) ->
                ReactorUtils.async(() -> results
                    .stream()
                    .filter(StreamUtils.distinctByKey(Game::id))
                    .collect(Collectors.toList())
                )
            )
            .map(OkGamesSearchResult::new);
    }
}
