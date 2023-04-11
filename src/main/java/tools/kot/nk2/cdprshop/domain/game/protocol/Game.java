package tools.kot.nk2.cdprshop.domain.game.protocol;

import lombok.NonNull;
import lombok.With;
import tools.kot.nk2.cdprshop.domain.tag.protocol.Tag;

import java.math.BigDecimal;
import java.util.List;

@With
public record Game(
    @NonNull Long id,
    @NonNull String title,
    @NonNull String description,
    @NonNull BigDecimal price,
    @NonNull List<Tag> tags
) {
}
