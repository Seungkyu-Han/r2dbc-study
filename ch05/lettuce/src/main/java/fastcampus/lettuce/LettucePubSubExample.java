package fastcampus.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LettucePubSubExample {

    @SneakyThrows
    public static void main(String[] args) {


        RedisClient redisClient = RedisClient.create(
                RedisURI.builder()
                        .withHost("localhost")
                        .withPort(6379)
                        .build()
        );


        StatefulRedisPubSubConnection<String, String> connection1 = redisClient.connectPubSub();

        RedisPubSubReactiveCommands<String, String> commands1 = connection1.reactive();

        var channel = "abc";

        commands1.subscribe(channel)
                .thenMany(commands1.observeChannels())
                .doOnNext(value -> log.info("channel: {}", value.getMessage()))
                .subscribe();

        Thread.sleep(1000);

        StatefulRedisConnection<String, String> connection = redisClient.connectPubSub();

        RedisReactiveCommands<String, String> commands2 = connection.reactive();

        commands2.publish(channel, "hello")
                .then(commands2.publish(channel, "world"))
                .subscribe();





        Thread.sleep(1000);
    }
}
