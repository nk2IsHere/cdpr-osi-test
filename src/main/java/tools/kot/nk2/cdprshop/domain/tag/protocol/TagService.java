package tools.kot.nk2.cdprshop.domain.tag.protocol;

import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TagService {
    Mono<TagByIdFindResult> findTagById(Long id);

    sealed interface TagByIdFindResult {
    }

    record OkTagByIdFindResult(
        Tag tag
    ) implements TagByIdFindResult {
    }

    record NotFoundTagByIdFindResult(
    ) implements TagByIdFindResult {
    }

    Mono<AllTagsFindResult> findAllTags(Pageable pageable);

    sealed interface AllTagsFindResult {
    }

    record OkAllTagsFindResult(
        List<Tag> tags
    ) implements AllTagsFindResult {
    }

    Mono<TagCreateResult> createTag(TagCreateRequest request);

    record TagCreateRequest(
        @NonNull Tag.TagType type,
        @NonNull String value
    ) {
    }

    sealed interface TagCreateResult {
    }

    record OkTagCreateResult(
        Tag tag
    ) implements TagCreateResult {
    }

    record DuplicateTagTagCreateResult(
    ) implements TagCreateResult {
    }

    record ValueEmptyTagCreateResult(
    ) implements TagCreateResult {
    }

    Mono<TagByIdDeleteResult> deleteTagById(Long id);

    sealed interface TagByIdDeleteResult {
    }

    record OkTagByIdDeleteResult(
    ) implements TagByIdDeleteResult {
    }

    record NotFoundTagByIdDeleteResult(
    ) implements TagByIdDeleteResult {
    }

    Mono<TagsByIdsFindResult> findTagsByIds(List<Long> ids);

    sealed interface TagsByIdsFindResult {
    }

    record OkTagsByIdsFindResult(
        List<Tag> tags
    ) implements TagsByIdsFindResult {
    }

    record NotFoundTagsByIdsFindResult(
        List<Tag> partialTags
    ) implements TagsByIdsFindResult {
    }
}
