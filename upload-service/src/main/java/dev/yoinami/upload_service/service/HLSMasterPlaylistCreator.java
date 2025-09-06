package dev.yoinami.upload_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HLSMasterPlaylistCreator {
    
    public void createMasterPlaylist(String outputDir, int width720, int width480, int width240) throws IOException {
        // Create the master playlist content
        String masterContent = "#EXTM3U\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=3000000,RESOLUTION=" + width720 + "x720\n" +
                "720p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=1500000,RESOLUTION=" + width480 + "x480\n" +
                "480p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=500000,RESOLUTION=" + width240 + "x240\n" +
                "240p.m3u8";
        
        // Write to file
        Path masterPath = Paths.get(outputDir, "master.m3u8");
        Files.write(masterPath, masterContent.getBytes());
        
        System.out.println("Master playlist created: " + masterPath.toString());
    }
    
    // If you need to automatically determine resolutions
    public void createMasterPlaylistWithAutoResolution(String outputDir, String originalVideoPath) throws IOException {
        // Use ffprobe to get the original video dimensions
        // This is a simplified example - you'd need to implement ffprobe integration
        VideoDimensions dims = getVideoDimensions(originalVideoPath);
        
        // Calculate scaled widths while maintaining aspect ratio
        int width720 = (int) Math.round((720.0 * dims.width) / dims.height);
        int width480 = (int) Math.round((480.0 * dims.width) / dims.height);
        int width240 = (int) Math.round((240.0 * dims.width) / dims.height);
        
        // Ensure even numbers (required by some codecs)
        width720 = width720 % 2 == 0 ? width720 : width720 - 1;
        width480 = width480 % 2 == 0 ? width480 : width480 - 1;
        width240 = width240 % 2 == 0 ? width240 : width240 - 1;
        
        createMasterPlaylist(outputDir, width720, width480, width240);
    }
    
    // Helper class and method to get video dimensions
    private static class VideoDimensions {
        int width;
        int height;
        
        VideoDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
    
    private VideoDimensions getVideoDimensions(String videoPath) {
        return new VideoDimensions(1280, 720);
    }
}