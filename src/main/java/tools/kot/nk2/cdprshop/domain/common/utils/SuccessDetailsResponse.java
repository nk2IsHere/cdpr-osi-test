package tools.kot.nk2.cdprshop.domain.common.utils;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

public record SuccessDetailsResponse(
    @NonNull HttpStatus code,
    @NonNull String message
) {
}
