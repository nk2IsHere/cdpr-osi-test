package tools.kot.nk2.cdprshop.domain.game;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends R2dbcRepository<GameEntity, Long> {
}
