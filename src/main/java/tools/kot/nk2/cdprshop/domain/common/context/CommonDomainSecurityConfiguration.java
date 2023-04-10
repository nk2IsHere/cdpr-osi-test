package tools.kot.nk2.cdprshop.domain.common.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@Configuration
public class CommonDomainSecurityConfiguration {

    @Bean
    public PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }
}
