package fastcampus.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LettuceListQueueExample {

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

        var queue1 = "queue:1";

        commands
                .lpush(queue1, "100")
                .then(commands.lpush(queue1, "200"))
                .then(commands.llen(queue1))
                .doOnNext(len -> log.info("queue:1 len: {}", len))
                .then(commands.rpop(queue1))
                .doOnNext(pop -> log.info("queue:1 pop: {}", pop))
                .then(commands.rpop(queue1))
                .doOnNext(pop -> log.info("queue:1 pop: {}", pop))
                .subscribe();

        Thread.sleep(1000);
    }
}
