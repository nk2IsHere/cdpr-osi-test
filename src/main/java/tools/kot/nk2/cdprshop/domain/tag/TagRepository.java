package tools.kot.nk2.cdprshop.domain.tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.kot.nk2.cdprshop.domain.tag.protocol.Tag;

@Repository
public interface TagRepository extends R2dbcRepository<TagEntity, Long> {
    Flux<TagEntity> findAllBy(Pageable pageable);

    Mono<Boolean> existsByTypeAndValue(Tag.TagType type, String value);
}
