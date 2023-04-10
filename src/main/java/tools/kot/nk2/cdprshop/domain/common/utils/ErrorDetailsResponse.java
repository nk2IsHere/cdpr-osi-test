package tools.kot.nk2.cdprshop.domain.common.utils;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

public record ErrorDetailsResponse(
    @NonNull HttpStatus code,
    @NonNull String reason
) {
}
