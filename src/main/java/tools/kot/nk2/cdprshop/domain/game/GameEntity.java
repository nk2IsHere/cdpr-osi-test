package tools.kot.nk2.cdprshop.domain.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;
import tools.kot.nk2.cdprshop.domain.common.utils.BaseEntity;
import tools.kot.nk2.cdprshop.domain.game.protocol.Game;
import tools.kot.nk2.cdprshop.domain.tag.protocol.Tag;

import java.math.BigDecimal;
import java.util.List;

@Table
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class GameEntity extends BaseEntity<Game> {
    private String title;

    private String description;

    private BigDecimal price;

    private List<Tag> tags;

    @Override
    public Game toResource() {
        return new Game(
            id,
            title,
            description,
            price,
            tags
        );
    }
}
