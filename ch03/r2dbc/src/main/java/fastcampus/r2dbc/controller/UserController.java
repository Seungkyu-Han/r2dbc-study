package fastcampus.r2dbc.controller;

import fastcampus.r2dbc.common.User;
import fastcampus.r2dbc.controller.dto.ProfileImageResponse;
import fastcampus.r2dbc.controller.dto.SignupUserRequest;
import fastcampus.r2dbc.controller.dto.UserResponse;
import fastcampus.r2dbc.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public Mono<UserResponse> getUserById(
            @PathVariable Long userId
    ){
        log.info("userId={}", userId);

        return ReactiveSecurityContextHolder
                .getContext()
                .flatMap(
                        context -> {

                            String name = context.getAuthentication().getName();

                            if(!Objects.equals(name, userId.toString())) {
                                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                            }


                            return userService.findById(userId)
                                    .switchIfEmpty(
                                            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))
                                    )
                                    .map(this::map);
                        }
                );

    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponse> signupUser(
            @RequestBody SignupUserRequest signupUserRequest){

        return userService.createUser(
                signupUserRequest.getName(),
                signupUserRequest.getAge(),
                signupUserRequest.getPassword(),
                signupUserRequest.getProfileImageId()
        ).map(this::map);
    }

    private UserResponse map(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getFollowCount(),
                user.getProfileImage().map(image ->
                        new ProfileImageResponse(
                                image.getId(),
                                image.getName(),
                                image.getUrl()))
        );
    }
}
