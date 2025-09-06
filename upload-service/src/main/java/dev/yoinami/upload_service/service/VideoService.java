package dev.yoinami.upload_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.yoinami.upload_service.model.Video;
import dev.yoinami.upload_service.repository.VideoRepository;
import reactor.core.publisher.Mono;

@Service
public class VideoService {
    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Mono<Video> createVideo(String title) {
        Video video = new Video();
        video.setId(UUID.randomUUID().toString());
        video.setTitle(title);
        video.setNew(true);
        return videoRepository.save(video);
    }
}
