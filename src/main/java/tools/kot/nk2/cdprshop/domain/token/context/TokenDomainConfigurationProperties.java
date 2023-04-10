package tools.kot.nk2.cdprshop.domain.token.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "cdprshop.token")
public class TokenDomainConfigurationProperties {

    @Getter
    @Setter
    private Long tokenExpirationMillis;

    @Getter
    @Value("classpath:/private.key.der")
    private Resource privateKeyResource;

    @Getter
    @Value("classpath:/public.key.der")
    private Resource publicKeyResource;
}
