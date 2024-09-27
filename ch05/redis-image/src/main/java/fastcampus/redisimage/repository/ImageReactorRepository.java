package fastcampus.redisimage.repository;


import fastcampus.redisimage.entity.common.EmptyImage;
import fastcampus.redisimage.entity.common.repository.ImageEntity;
import fastcampus.redisimage.entity.common.repository.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class ImageReactorRepository {
    private final ReactiveHashOperations<String, String, String> hashOperations;

    public ImageReactorRepository(
            ReactiveStringRedisTemplate redisTemplate
    ) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    @SneakyThrows
    public Mono<ImageEntity> findById(String id) {
        return hashOperations.multiGet(id, List.of("id", "name", "url"))
                .handle((strings, sink) -> {

                    if(strings.stream().allMatch(Objects::isNull)) {
                        sink.error(new RuntimeException());
                        return;
                    }

                    sink.next(new ImageEntity(
                            strings.get(0),
                            strings.get(1),
                            strings.get(2)

                    ));
                });
    }

    public Mono<ImageEntity> findWithContext() {
        return Mono.deferContextual(context -> {
            Optional<UserEntity> userOptional = context.getOrEmpty("user");
            if (userOptional.isEmpty()) throw new RuntimeException("user not found");

            return Mono.just(userOptional.get().getProfileImageId());
        }).flatMap(this::findById);
    }

    public Mono<ImageEntity> create(String id, String name, String url) {
        return hashOperations
                .putAll(id, Map.of("id", id, "name", name, "url", url))
                .then(findById(id));
    }
}
