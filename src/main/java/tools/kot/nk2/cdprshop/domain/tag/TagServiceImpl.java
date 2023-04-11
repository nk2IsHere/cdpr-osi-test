package tools.kot.nk2.cdprshop.domain.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.kot.nk2.cdprshop.domain.tag.protocol.TagService;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository repository;

    @Autowired
    public TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<TagByIdFindResult> findTagById(Long id) {
        return repository
            .findById(id)
            .map((tag) -> new OkTagByIdFindResult(tag.toResource()))
            .cast(TagByIdFindResult.class)
            .defaultIfEmpty(new NotFoundTagByIdFindResult());
    }

    @Override
    public Mono<AllTagsFindResult> findAllTags(Pageable pageable) {
        return repository
            .findAllBy(pageable)
            .map(TagEntity::toResource)
            .collectList()
            .map(OkAllTagsFindResult::new);
    }

    @Override
    public Mono<TagsSaveResult> saveTags(List<TagsSaveRequest> tags) {
        return Flux
            .fromIterable(tags)
            .filterWhen((tag) ->
                repository
                    .existsByTypeAndValue(tag.type(), tag.value())
                    .map((exists) -> !exists)
            )
            .flatMap((tag) ->
                repository
                    .save(
                        new TagEntity()
                            .setType(tag.type())
                            .setValue(tag.value())
                    )
            )
            .map(TagEntity::toResource)
            .collectList()
            .map((result) ->
                result.size() == tags.size()
                    ? new OkTagsSaveResult(result)
                    : new DuplicatesFoundTagsSaveResult(result)
            );
    }

    @Override
    public Mono<TagByIdDeleteResult> deleteTagById(Long id) {
        return repository
            .existsById(id)
            .filter((exists) -> exists)
            .flatMap((exists) -> repository.deleteById(id))
            .thenReturn(new OkTagByIdDeleteResult())
            .cast(TagByIdDeleteResult.class)
            .defaultIfEmpty(new NotFoundTagByIdDeleteResult());
    }
}
