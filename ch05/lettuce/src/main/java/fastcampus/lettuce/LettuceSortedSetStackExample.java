package fastcampus.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class LettuceSortedSetStackExample {

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

        var zset1 = "zset:1";

        commands.zadd(zset1,
                        ScoredValue.just(10.0, "a"),
                        ScoredValue.just(1.0, "b"),
                        ScoredValue.just(0.1, "c"),
                        ScoredValue.just(100.0, "d")
                )
                .then(commands.zrem(zset1, "d"))
                .doOnNext(result -> log.info("result: {}", result))
                .then(commands.zcard(zset1))
                .doOnNext(result -> log.info("zset len: {}", result))
                .thenMany(commands.zrangeWithScores(zset1, 0, -1))
                .doOnNext(item -> log.info("item: {}", item))
                .then(commands.zrank(zset1, "a"))
                .doOnNext(item -> log.info("item: {}", item))
                .subscribe()

        ;

        Thread.sleep(1000);
    }
}
