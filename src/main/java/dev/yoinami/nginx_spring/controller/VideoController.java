package dev.yoinami.nginx_spring.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.yoinami.nginx_spring.model.Video;
import dev.yoinami.nginx_spring.service.VideoService;
import reactor.core.publisher.Mono;

@Controller
public class VideoController {

    private final String UPLOAD_DIR;
    private final VideoService videoService;

    VideoController(@Value("${file.video-dir}") String uploadDir, VideoService videoService) {
        this.UPLOAD_DIR = uploadDir;
        this.videoService = videoService;

        if (UPLOAD_DIR == null || UPLOAD_DIR.isBlank()) {
            throw new IllegalArgumentException(
                    "Upload directory must be configured. Set `file.video-dir` in application.yml");
        }
    }

    @PostMapping(value = "/video/upload")
    public Mono<ResponseEntity<String>> uploadVideo(@RequestPart("file") FilePart filePart) {

        // Ensure the output directory exists
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Safely join the directory and filename
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filePart.filename()).normalize();
        Mono<Video> videoMono = videoService.createVideo(filePart.filename());

        return filePart
                .transferTo(filePath.toFile())
                .then(videoMono)
                .then(Mono.fromRunnable(() -> videoService.transcodeVideo(filePath.toString())))
                .then(Mono.just(ResponseEntity.ok("Upload Successful")))
                .onErrorResume(
                        e -> Mono.just(ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage())));
    }

    @GetMapping(value = "/watch/{video_id}", produces = "application/vnd.apple.mpegurl")
    @ResponseBody
    @CrossOrigin(origins = "http://localhost:3000")
    public Mono<Resource> watchVideo(@PathVariable String video_id) {
        if (!video_id.endsWith(".m3u8") && !video_id.endsWith(".ts")) {
            video_id = video_id + "_master.m3u8";
        }
        Path playlist = Paths.get(UPLOAD_DIR, video_id);
        return Mono.just(new FileSystemResource(playlist));
    }
}