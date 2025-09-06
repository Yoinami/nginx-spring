package dev.yoinami.transcoder_service.util;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Service
public class TranscodeVideo {
    private final String ffmpegDir;
    private final String ffprobeDir;

    public TranscodeVideo(
            @Value("${file.ffmpeg-dir}") String ffmpegDir,
            @Value("${file.ffprobe-dir}") String ffprobeDir) {
        this.ffmpegDir = ffmpegDir;
        this.ffprobeDir = ffprobeDir;
    }

    public void transcodeVideo(String filePath) {
        System.out.println("Transcoding started for: " + filePath);
        try {
            transcodeToHLS(filePath);
            createMasterPlaylist(filePath);
            System.out.println("Transcoding completed for: " + filePath);
        } catch (IOException e) {
            System.err.println("Transcoding failed for: " + filePath);
            e.printStackTrace();
        }   
    }

    public void transcodeToHLS(String inputPath) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegDir);
        FFprobe ffprobe = new FFprobe(ffprobeDir);

        String baseName = inputPath.substring(inputPath.lastIndexOf('\\') + 1, inputPath.lastIndexOf('.'));
        String outputDir = Paths.get(inputPath).getParent().toString();

        FFmpegBuilder builder720p = new FFmpegBuilder()
                .setInput(inputPath)
                .addOutput(outputDir + "\\" + baseName + "_720p.m3u8")
                .setFormat("hls")
                .addExtraArgs("-hls_time", "4")
                .addExtraArgs("-hls_playlist_type", "vod")
                .setVideoFilter("scale=-2:720")
                .setVideoCodec("h264")
                .setVideoBitRate(3000000) // 3000k
                .setAudioCodec("aac")
                .addExtraArgs("-strict", "-2")
                .done();

        FFmpegBuilder builder480p = new FFmpegBuilder()
                .setInput(inputPath)
                .addOutput(outputDir + "\\" + baseName + "_480p.m3u8")
                .setFormat("hls")
                .addExtraArgs("-hls_time", "4")
                .addExtraArgs("-hls_playlist_type", "vod")
                .setVideoFilter("scale=-2:480")
                .setVideoCodec("h264")
                .setVideoBitRate(1500000) // 1500k
                .setAudioCodec("aac")
                .addExtraArgs("-strict", "-2")
                .done();

        FFmpegBuilder builder240p = new FFmpegBuilder()
                .setInput(inputPath)
                .addOutput(outputDir + "\\" + baseName + "_240p.m3u8")
                .setFormat("hls")
                .addExtraArgs("-hls_time", "4")
                .addExtraArgs("-hls_playlist_type", "vod")
                .setVideoFilter("scale=-2:240")
                .setVideoCodec("h264")
                .setVideoBitRate(500000) // 500k
                .setAudioCodec("aac")
                .addExtraArgs("-strict", "-2")
                .done();

        // Execute each conversion
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        // Run each conversion (note: this will process the video 3 times)
        executor.createJob(builder720p).run();
        executor.createJob(builder480p).run();
        executor.createJob(builder240p).run();

    }

    public void createMasterPlaylist(String inputPath) throws IOException {
        String baseName = inputPath.substring(inputPath.lastIndexOf('\\') + 1, inputPath.lastIndexOf('.'));
        String outputDir = Paths.get(inputPath).getParent().toString();
        String masterContent = "#EXTM3U\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=3000000,RESOLUTION=1280x720\n" +
                baseName + "_720p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=1500000,RESOLUTION=854x480\n" +
                baseName + "_480p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=500000,RESOLUTION=426x240\n" +
                baseName + "_240p.m3u8";

        java.nio.file.Files.write(
                Paths.get(outputDir, baseName + "_master.m3u8"),
                masterContent.getBytes());
    }
}
