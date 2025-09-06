package dev.yoinami.upload_service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import dev.yoinami.upload_service.model.Video;

public interface VideoRepository extends ReactiveCrudRepository<Video, String> {
}
