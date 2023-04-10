package tools.kot.nk2.cdprshop.domain.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;
import tools.kot.nk2.cdprshop.domain.common.utils.BaseEntity;
import tools.kot.nk2.cdprshop.domain.user.protocol.User;

@Table
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class UserEntity extends BaseEntity<User> {

    private String username;

    private String password;

    private User.UserRole role;

    public User toResource() {
        return new User(
            id,
            username,
            role
        );
    }
}
