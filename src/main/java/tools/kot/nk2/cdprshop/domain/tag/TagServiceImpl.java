package tools.kot.nk2.cdprshop.domain.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
    public Mono<TagCreateResult> createTag(TagCreateRequest request) {
        return repository
            .existsByTypeAndValue(request.type(), request.value())
            .filter((exists) -> !exists)
            .flatMap((exists) ->
                Mono.just(request.value().isEmpty())
                    .filter((empty) -> !empty)
                    .flatMap((empty) -> repository
                        .save(
                            new TagEntity()
                                .setType(request.type())
                                .setValue(request.value())
                        )
                    )
                    .map((tag) -> new OkTagCreateResult(tag.toResource()))
                    .cast(TagCreateResult.class)
                    .defaultIfEmpty(new ValueEmptyTagCreateResult())
            )
            .defaultIfEmpty(new DuplicateTagTagCreateResult());
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

    @Override
    public Mono<TagsByIdsFindResult> findTagsByIds(List<Long> ids) {
        return repository
            .findAllById(ids)
            .map(TagEntity::toResource)
            .collectList()
            .map((result) ->
                result.size() == ids.size()
                    ? new OkTagsByIdsFindResult(result)
                    : new NotFoundTagsByIdsFindResult(result)
            );
    }
}
