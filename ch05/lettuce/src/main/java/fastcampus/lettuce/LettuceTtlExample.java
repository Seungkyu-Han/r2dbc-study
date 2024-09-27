package fastcampus.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LettuceTtlExample {

    @SneakyThrows
    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create(
                RedisURI.builder()
                        .withHost("localhost")
                        .withPort(6379)
                        .build()
        );

        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisReactiveCommands<String, String> commands = connection.reactive();

        var nameKey = "person1:name";

        commands.set(nameKey, "seungkyu")
                .then(commands.expire(nameKey, 5))
                .then(commands.ttl(nameKey))
                .doOnNext(ttl -> log.info("ttl: {}", ttl))
                .then(commands.get(nameKey))
                .doOnNext(value -> log.info("value: {}", value))
                .subscribe();


        Thread.sleep(7000);


        commands.ttl(nameKey)
                .doOnNext(ttl -> log.info("ttl: {}", ttl))
                .then(commands.get(nameKey))
                .defaultIfEmpty("empty!!")
                .doOnNext(value -> log.info("value: {}", value))
                .subscribe()

        ;


        Thread.sleep(1000);
    }
}
