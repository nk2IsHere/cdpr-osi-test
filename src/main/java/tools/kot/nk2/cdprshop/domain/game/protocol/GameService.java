package tools.kot.nk2.cdprshop.domain.game.protocol;

import lombok.NonNull;
import lombok.With;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface GameService {
    Mono<GameByIdFindResult> findGameById(Long id);

    sealed interface GameByIdFindResult {
    }

    record OkGameByIdFindResult(
        Game game
    ) implements GameByIdFindResult {
    }

    record NotFoundGameByIdFindResult(
    ) implements GameByIdFindResult {
    }
    
    Mono<GameCreateResult> createGame(GameCreateRequest request);

    @With
    record GameCreateRequest(
        @NonNull String title,
        @NonNull String description,
        @NonNull BigDecimal price,
        @NonNull List<Long> tags
    ) {
    }

    sealed interface GameCreateResult {
    }

    record OkGameCreateResult(
        Game game
    ) implements GameCreateResult {
    }

    record TagsNotFoundFoundGameCreateResult(
    ) implements GameCreateResult {
    }

    record TitleOrDescriptionEmptyGameCreateResult(
    ) implements GameCreateResult {
    }

    Mono<GameByIdDeleteResult> deleteGameById(Long id);

    sealed interface GameByIdDeleteResult {
    }

    record OkGameByIdDeleteResult(
    ) implements GameByIdDeleteResult {
    }

    record NotFoundGameByIdDeleteResult(
    ) implements GameByIdDeleteResult {
    }

    Mono<GameByIdUpdateResult> updateGameById(GameByIdUpdateRequest request);

    @With
    record GameByIdUpdateRequest(
        Long id,
        String title,
        String description,
        BigDecimal price,
        List<Long> tags
    ) {
    }

    sealed interface GameByIdUpdateResult {
    }

    record OkGameByIdUpdateResult(
        Game game
    ) implements GameByIdUpdateResult {
    }

    record NotFoundGameByIdUpdateResult(
    ) implements GameByIdUpdateResult {
    }

    record TagsNotFoundGameByIdUpdateResult(
    ) implements GameByIdUpdateResult {
    }

    record TitleOrDescriptionEmptyGameByIdUpdateResult(
    ) implements GameByIdUpdateResult {
    }
}
