package fastcampus.redisimage.handler;


import fastcampus.redisimage.handler.dto.CreateRequest;
import fastcampus.redisimage.handler.dto.ImageResponse;
import fastcampus.redisimage.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class ImageHandler {
    private final ImageService imageService;

    public Mono<ServerResponse> getImageById(ServerRequest serverRequest) {
        String imageId = serverRequest.pathVariable("imageId");

        return imageService.getImageById(imageId)
                .map(image ->
                        new ImageResponse(image.getId(), image.getName(), image.getUrl())
                ).flatMap(imageResp ->
                        ServerResponse.ok().bodyValue(imageResp)
                ).onErrorMap(e -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Mono<ServerResponse> addImage(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(
                CreateRequest.class
                )
                .flatMap(
                createRequest -> imageService.saveImage(createRequest.getId(), createRequest.getName(), createRequest.getUrl())
        ).flatMap(
                imageResponse -> ServerResponse.ok().bodyValue(imageResponse)
        );
    }
}