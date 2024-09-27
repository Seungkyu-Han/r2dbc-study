package fastcampus.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LettuceSetExample {

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

        var set1 = "set:1";

        commands
                .sadd(set1, "100")
                .doOnNext(added -> log.info("added: {}", added))
                .then(commands.sadd(set1, "100", "200", "300"))
                .doOnNext(added -> log.info("added: {}", added))
                .thenMany(commands.smembers(set1))
                .doOnNext(member -> log.info("member: {}", member))
                .subscribe()

        ;



        Thread.sleep(1000);
    }
}
