package fastcampus.r2dbc.service;

import fastcampus.r2dbc.common.repository.AuthEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private static final Map<String, Long> tokenMap = Map.of("abcd", 4L);
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<Long> getNameByToken(String token) {
        var query = Query.query(
                Criteria.where("token").is(token)
        );

        return r2dbcEntityTemplate
                .select(AuthEntity.class)
                .matching(query)
                .one()
                .map(
                        AuthEntity::getUserId
                )
                .doOnNext(userId -> log.info("[getNameByToken] userId={}", userId));
    }

}
