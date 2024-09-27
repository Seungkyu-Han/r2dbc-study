package fastcampus.r2dbc.runner;

import fastcampus.r2dbc.common.repository.UserEntity;
import fastcampus.r2dbc.reactor.r2dbc.UserR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonInsertRunner implements CommandLineRunner {

    private final UserR2dbcRepository userRepository;
    @Override
    public void run(String... args) throws Exception {
        var newUser = new UserEntity("seungkyu", 20, "1", "1204");
        var savedUser = userRepository.save(newUser).block();

        log.info("user: {}", savedUser);

    }
}
