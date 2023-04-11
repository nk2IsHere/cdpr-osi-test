package tools.kot.nk2.cdprshop.domain.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;
import tools.kot.nk2.cdprshop.domain.common.utils.BaseEntity;
import tools.kot.nk2.cdprshop.domain.tag.protocol.Tag;

@Table
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class TagEntity extends BaseEntity<Tag> {

    private Tag.TagType type;

    private String value;

    @Override
    public Tag toResource() {
        return new Tag(
            id,
            type,
            value
        );
    }
}
