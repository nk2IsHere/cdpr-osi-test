package tools.kot.nk2.cdprshop.domain.token.protocol;

import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface TokenService {
    Mono<TokenGenerateResult> generateToken(String data);

    sealed interface TokenGenerateResult {
    }

    record OkTokenGenerateResult(
        @NonNull String data
    ) implements TokenGenerateResult {
    }

    Mono<TokenParseResult> parseToken(String token);

    sealed interface TokenParseResult {
    }

    record ValidTokenParseResult(
        @NonNull String data
    ) implements TokenParseResult {
    }

    record ExpiredTokenParseResult(
        @NonNull String data
    ) implements TokenParseResult {
    }

    record InvalidTokenParseResult(
    ) implements TokenParseResult {
    }
}
