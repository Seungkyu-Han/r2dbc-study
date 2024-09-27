package fastcampus.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class LettuceHashExample {

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

        var hash = "person:1";

        var fieldMap = Map.of("name", "seungkyu", "age", "20", "gender", "M");

        commands.hset(hash, fieldMap)
                .thenMany(commands.hgetall(hash))
                .doOnNext(item -> log.info("item: {}", item))
                .thenMany(commands.hmget(hash, "name", "age"))
                .doOnNext(item -> log.info("item: {}", item))
                .subscribe();


        Thread.sleep(1000);
    }
}
