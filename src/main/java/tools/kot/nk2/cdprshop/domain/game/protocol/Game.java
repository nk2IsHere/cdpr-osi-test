package tools.kot.nk2.cdprshop.domain.game.protocol;

import lombok.NonNull;
import lombok.With;

import java.math.BigDecimal;

@With
public record Game(
    @NonNull Long id,
    @NonNull String title,
    @NonNull String description,
    @NonNull BigDecimal price,
    @NonNull
) {
}
