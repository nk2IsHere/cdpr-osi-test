package tools.kot.nk2.cdprshop.domain.common.utils;

import org.springframework.web.reactive.function.server.ServerRequest;
import tools.kot.nk2.cdprshop.domain.user.protocol.User;

import java.util.Optional;

public class WebUtils {
    public static Optional<User> getCurrentUser(ServerRequest request) {
        return request
            .attribute("currentUser")
            .map((user) -> (User) user);
    }
}
