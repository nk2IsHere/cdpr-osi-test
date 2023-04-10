package tools.kot.nk2.cdprshop.domain.user.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.kot.nk2.cdprshop.domain.user.protocol.User;
import tools.kot.nk2.cdprshop.domain.user.protocol.UserService;

import java.util.Objects;
import java.util.logging.Logger;

@Component
public class UserDomainCreateDefaultSystemUserTask {

    private final Logger log = Logger.getLogger(UserDomainCreateDefaultSystemUserTask.class.getName());

    private final UserService userService;
    private final UserDomainConfigurationProperties properties;

    @Autowired
    public UserDomainCreateDefaultSystemUserTask(
        UserService userService,
        UserDomainConfigurationProperties properties
    ) {
        this.userService = userService;
        this.properties = properties;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void createDefaultSystemUserTask() {
        if(properties.getDefaultSystemUserUsername() == null || properties.getDefaultSystemUserPassword() == null) {
            log.info("Default system user is not created: no property data was provided.");
            return;
        }

        var result = userService
            .createUser(new UserService.UserCreateRequest(
                properties.getDefaultSystemUserUsername(),
                properties.getDefaultSystemUserPassword(),
                User.UserRole.SYSTEM
            ))
            .block();

        switch (Objects.requireNonNull(result)) {
            case UserService.DuplicateUsernameUserCreateResult duplicateUsernameUserCreateResult -> {
                log.info("Default system user is not created: such user already exists.");
            }
            case UserService.OkUserCreateResult okUserCreateResult -> {
                log.info("Default system user is created.");
            }
        }
    }
}
