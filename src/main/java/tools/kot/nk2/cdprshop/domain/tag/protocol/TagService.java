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

    Mono<TagsSaveResult> saveTags(List<TagsSaveRequest> tags);

    record TagsSaveRequest(
        @NonNull Tag.TagType type,
        @NonNull String value
    ) {
    }

    sealed interface TagsSaveResult {
    }

    record OkTagsSaveResult(
        List<Tag> tags
    ) implements TagsSaveResult {
    }

    record DuplicatesFoundTagsSaveResult(
        List<Tag> tags
    ) implements TagsSaveResult {
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
}
