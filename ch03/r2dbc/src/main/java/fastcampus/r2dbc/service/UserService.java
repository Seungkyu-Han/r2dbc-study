package fastcampus.r2dbc.service;

import fastcampus.r2dbc.common.EmptyImage;
import fastcampus.r2dbc.common.Image;
import fastcampus.r2dbc.common.User;
import fastcampus.r2dbc.common.repository.AuthEntity;
import fastcampus.r2dbc.common.repository.UserEntity;
import fastcampus.r2dbc.reactor.r2dbc.UserR2dbcRepository;
import fastcampus.r2dbc.service.response.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private WebClient webClient = WebClient.create("http://localhost:8081");

    private final UserR2dbcRepository userReactorRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<User> findById(Long userId){
        return userReactorRepository.findById(userId)
                .flatMap(userEntity -> {
                    String imageId = userEntity.getProfileImageId();
                    return webClient.get().uri("/api/images/{imageId}", imageId)
                            .retrieve()
                            .bodyToMono(ImageResponse.class)
                            .map(
                                    response -> new Image(
                                            response.getId(),
                                            response.getName(),
                                            response.getUrl()
                                    )

                            ).onErrorComplete()
                            .switchIfEmpty(Mono.just(new EmptyImage()))
                            .map(image -> new User(
                                    userEntity.getId().toString(),
                                    userEntity.getName(),
                                    userEntity.getAge(),
                                    Optional.of(image),
                                    List.of(),
                                    0L
                            ));
                        }
                );
    }

    @Transactional
    public Mono<User> createUser(String name, Integer age, String password, String profileImageId){

        var newUser = new UserEntity(
                name, age, password, profileImageId);

        var token = UUID.randomUUID().toString().substring(0, 6);

        return userReactorRepository
                .save(newUser)
                .flatMap(
                        userEntity -> {
                            AuthEntity auth = new AuthEntity(userEntity.getId(), token);

                            return r2dbcEntityTemplate.insert(auth)
                                    .map(authEntity -> userEntity);
                        }
                )
                .map(
                        userEntity -> map(userEntity, Optional.of(new EmptyImage()))
                );
    }

    private User map(UserEntity userEntity, Optional<Image> profileImage){
        return new User(
                userEntity.getId().toString(),
                userEntity.getName(),
                userEntity.getAge(),
                profileImage,
                List.of(),
                0L
        );
    }

}
