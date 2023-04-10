package tools.kot.nk2.cdprshop.domain.user.protocol;

import lombok.NonNull;
import lombok.With;

@With
public record User(
    @NonNull Long id,
    @NonNull String username,
    @NonNull UserRole role
) {

    public enum UserRole {
        SYSTEM,
        ADMIN,
        VISITOR
    }
}
