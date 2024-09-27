package fastcampus.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class LettuceStringExample {

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


        var mono = commands.set("person1:name", "seungkyu")
                .then(commands.set("person1:age", "20"))
                .then(commands.setnx("person1:name", "notchanged"))
                .then(commands.get("person1:name"))
                .then(commands.get("person1:name"))
                .doOnNext(name -> log.info("name: {}", name))
                .thenMany(commands.mget("person1:name", "person1:age"))
                .doOnNext(keyValue -> log.info("keyValue: {}", keyValue))
                .then(commands.incrby("person1:age", 10))
                .doOnNext(keyValue -> log.info("value: {}", keyValue))
                .subscribe();

        Thread.sleep(1000);

    }
}
