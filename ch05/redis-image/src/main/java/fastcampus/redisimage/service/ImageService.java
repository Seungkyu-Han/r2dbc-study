package fastcampus.redisimage.service;


import fastcampus.redisimage.entity.common.Image;
import fastcampus.redisimage.entity.common.repository.ImageEntity;
import fastcampus.redisimage.repository.ImageReactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageReactorRepository imageRepository;

    public Mono<Image> getImageById(String imageId) {
        return imageRepository.findById(imageId)
                .map(imageEntity ->
                        new Image(
                                imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()
                        )
                );
    }

    public Mono<Image> saveImage(String id, String name, String url) {
        return imageRepository.create(id, name, url)
                .map(this::map);
    }

    public Image map(ImageEntity imageEntity) {
        return new Image(
                imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()
        );
    }
}