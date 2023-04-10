package tools.kot.nk2.cdprshop.domain.user.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cdprshop.user")
public class UserDomainConfigurationProperties {
    @Getter
    @Setter
    private String defaultSystemUserUsername;

    @Getter
    @Setter
    private String defaultSystemUserPassword;
}
