package fastcampus.r2dbc.reactor.r2dbc;

import fastcampus.r2dbc.common.repository.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface UserR2dbcRepository extends R2dbcRepository<UserEntity, Long> {
}
