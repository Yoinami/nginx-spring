package dev.yoinami.nginx_spring.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import dev.yoinami.nginx_spring.model.Video;

public interface VideoRepository extends ReactiveCrudRepository<Video, String> {
}
