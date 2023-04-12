package tools.kot.nk2.cdprshop.domain.game;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface GameRepository extends R2dbcRepository<GameEntity, Long> {

    Flux<GameEntity> findAllBy(Pageable pageable);

    @Query("SELECT * FROM game_entity where ts_title @@ to_tsquery('english', $1)")
    Flux<GameEntity> searchByTitle(String title);

    @Query("SELECT * FROM game_entity where ts_description @@ to_tsquery('english', $1)")
    Flux<GameEntity> searchByDescription(String description);
}
