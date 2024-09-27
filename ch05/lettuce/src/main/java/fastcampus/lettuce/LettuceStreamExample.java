package fastcampus.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Map;


@Slf4j
public class LettuceStreamExample {

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

        var streamName = "stream:1";

        var readArgs = XReadArgs.Builder
                .block(10_000)
                .count(2);

        var streamOffset = XReadArgs.StreamOffset
                        .latest(streamName);

        commands.xread(readArgs, streamOffset)
                .doOnSubscribe(
                        subscriber -> {
                            log.info("sub");
                        }
                )
                .subscribe(
                        message -> {
                            log.info("message: {}", message);
                        }
                );


        StatefulRedisConnection<String, String> connection2 = redisClient.connect();

        RedisReactiveCommands<String, String> commands2 = connection.reactive();

        commands2.multi()
                        .subscribe(multiResp -> {
                            Flux.mergeSequential(
                                    commands2.xadd(streamName, Map.of("hello1", "world1")),
                                    commands2.xadd(streamName, Map.of("hello1", "world1")),
                                    commands2.xadd(streamName, Map.of("hello1", "world1")),
                                    commands2.exec()
                            ).subscribe();
                        });


        Thread.sleep(10000);
    }
}
