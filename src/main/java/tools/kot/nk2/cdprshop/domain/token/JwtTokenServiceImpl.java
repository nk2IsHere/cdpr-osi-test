package tools.kot.nk2.cdprshop.domain.token;

import io.jsonwebtoken.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.kot.nk2.cdprshop.domain.common.utils.KeyUtils;
import tools.kot.nk2.cdprshop.domain.common.utils.ReactorUtils;
import tools.kot.nk2.cdprshop.domain.token.context.TokenDomainConfigurationProperties;
import tools.kot.nk2.cdprshop.domain.token.protocol.TokenService;

import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenServiceImpl implements TokenService {

    private final TokenDomainConfigurationProperties properties;

    @Autowired
    public JwtTokenServiceImpl(TokenDomainConfigurationProperties properties) {
        this.properties = properties;
    }

    @SneakyThrows
    @Override
    public Mono<TokenGenerateResult> generateToken(String data) {
        return ReactorUtils.async(() -> {
            var expiryDate = properties.getTokenExpirationMillis() != null?
                new Date(System.currentTimeMillis() + properties.getTokenExpirationMillis())
                : null;

            var token = Jwts.builder()
                .setSubject(data)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(
                    KeyUtils.getPrivateKey(properties.getPrivateKeyResource().getFile()),
                    SignatureAlgorithm.RS256
                )
                .compact();

            return new OkTokenGenerateResult(token);
        });
    }

    // Patch for singing method autodetection exploit
    private final SigningKeyResolver singingKeyResolver = new SigningKeyResolver() {

        private Boolean checkSingingAlgorithm(String algorithm) {
            return algorithm.equals(SignatureAlgorithm.RS256.getValue());
        }

        @SneakyThrows
        public Key resolveSigningKey(JwsHeader header, Claims claims) {
            if(!checkSingingAlgorithm(header.getAlgorithm())) {
                throw new UnsupportedJwtException("Unsupported algorithm signature detected! Required RS256.");
            }

            return KeyUtils
                .getPrivateKey(
                    properties
                        .getPrivateKeyResource()
                        .getFile()
                );
        }

        @SneakyThrows
        public Key resolveSigningKey(JwsHeader header, String plaintext) {
            if(!checkSingingAlgorithm(header.getAlgorithm())) {
                throw new UnsupportedJwtException("Unsupported algorithm signature detected! Required RS256.");
            }

            return KeyUtils
                .getPublicKey(
                    properties
                        .getPublicKeyResource()
                        .getFile()
                );
        }
    };

    @Override
    public Mono<TokenParseResult> parseToken(String token) {
        return ReactorUtils.async(() -> {
            try {
                var tokenParseResult = Jwts.parserBuilder()
                    .setSigningKeyResolver(singingKeyResolver)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

                return new ValidTokenParseResult(tokenParseResult);
            } catch (ExpiredJwtException e) {
                return new ExpiredTokenParseResult(
                    e.getClaims()
                        .getSubject()
                );
            }  catch (SecurityException | UnsupportedJwtException | IllegalArgumentException | MalformedJwtException ignored) {
            }

            return new InvalidTokenParseResult();
        });
    }
}
