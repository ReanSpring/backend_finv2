package com.example.finv2.service;

import com.example.finv2.model.Video;
import com.example.finv2.repo.VideoRepo;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.List;

@Service
public class VideoService {
    private final VideoRepo videoRepository;

    public VideoService(VideoRepo videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<Video> findAllVideos() {
        return videoRepository.findAll();
    }

    public Video findVideoById(Long id) {
        return videoRepository.findById(id).orElse(null);
    }

    public Video saveVideo(Video video) {
        return videoRepository.save(video);
    }

    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }

    public void downloadVideo(String videoUrl, String outputFilePath) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(videoUrl);
            URLConnection urlConnection = url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String downloadPath = Paths.get(System.getProperty("user.home"), "Downloads", outputFilePath).toString();
            fileOutputStream = new FileOutputStream(downloadPath);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Download completed successfully. File saved to: " + downloadPath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Video downloadAndSaveVideo(Video video) {
        downloadVideo(video.getUrl(), video.getFilePath());
        return saveVideo(video);
    }
}