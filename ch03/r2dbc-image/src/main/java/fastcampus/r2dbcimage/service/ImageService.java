package fastcampus.r2dbcimage.service;

import fastcampus.r2dbcimage.entity.common.Image;
import fastcampus.r2dbcimage.repository.ImageReactorRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ImageService {

    private ImageReactorRepository imageReactorRepository = new ImageReactorRepository();

    public Mono<Image> getImageById(String imageId){
        return imageReactorRepository.findById(imageId)
                .map(imageEntity ->
                        new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()));
    }
}
