package tools.kot.nk2.cdprshop.domain.user;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
    Mono<UserEntity> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);
}
