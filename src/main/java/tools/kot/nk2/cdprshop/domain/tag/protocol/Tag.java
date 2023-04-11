package tools.kot.nk2.cdprshop.domain.tag.protocol;

import lombok.NonNull;
import lombok.With;

@With
public record Tag(
    @NonNull Long id,
    @NonNull TagType type,
    String value
) {

    public enum TagType {
        SPECIAL_PRICING_IS_FREE,
        SPECIAL_PRICING_IS_ON_PROMOTION,
        META_GENRE,
        META_PUBLISHER,
        META_DEVELOPER
    }
}
