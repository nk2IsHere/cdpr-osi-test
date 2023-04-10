package tools.kot.nk2.cdprshop.domain.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;
import tools.kot.nk2.cdprshop.domain.common.utils.BaseEntity;
import tools.kot.nk2.cdprshop.domain.game.protocol.Game;

@Table
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class GameEntity extends BaseEntity<Game> {
    private String title;

    private String description;

    private String genre;

    @Override
    public Game toResource() {
        return new Game(
            id,
            title,
            description,
            genre
        );
    }
}
