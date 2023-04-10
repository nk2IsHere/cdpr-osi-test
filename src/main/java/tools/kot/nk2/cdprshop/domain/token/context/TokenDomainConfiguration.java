package tools.kot.nk2.cdprshop.domain.token.context;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TokenDomainConfigurationProperties.class)
public class TokenDomainConfiguration {
}
