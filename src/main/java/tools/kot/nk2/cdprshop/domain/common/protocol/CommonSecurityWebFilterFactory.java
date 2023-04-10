package tools.kot.nk2.cdprshop.domain.common.protocol;

import lombok.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import tools.kot.nk2.cdprshop.domain.user.protocol.User;

import java.util.List;

public interface CommonSecurityWebFilterFactory {

    HandlerFilterFunction<ServerResponse, ServerResponse> create(List<FilterConfiguration> configurations);

    record FilterConfiguration(
        @NonNull String pathPattern,
        @NonNull HttpMethodMode methodMode,
        @NonNull RoleMode allowedRoles
    ) {
    }

    sealed interface HttpMethodMode {
    }

    record AllMethods(
    ) implements HttpMethodMode {
    }

    record SomeMethods(
        @NonNull List<HttpMethod> methods
    ) implements HttpMethodMode {
    }

    sealed interface RoleMode {
    }

    record AllRoles(
    ) implements RoleMode {
    }

    record SomeRoles(
        @NonNull List<User.UserRole> roles
    ) implements RoleMode {
    }
}
