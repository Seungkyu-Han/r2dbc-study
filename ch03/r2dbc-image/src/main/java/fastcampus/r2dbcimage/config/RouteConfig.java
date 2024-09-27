package fastcampus.r2dbcimage.config;

import fastcampus.r2dbcimage.handler.ImageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouteConfig {

    @Bean
    RouterFunction<ServerResponse> router(
            ImageHandler imageHandler
    ){
        return route()
                .path(
                        "/api", b1 -> b1
                                .path(
                                        "/images", b2 -> b2
                                                .GET("/{imageId:[0-9]+}", imageHandler::getImageById)
                                )
                )
                .build();
    }
}
