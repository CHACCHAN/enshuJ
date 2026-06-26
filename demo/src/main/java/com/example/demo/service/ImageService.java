package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.upload-dir:uploads/shop-images}")
    private String uploadDir;

    @Value("${app.avatar-dir:uploads/avatars}")
    private String avatarDir;

    public String save(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir).toAbsolutePath();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        Path filePath = dir.resolve(filename);
        file.transferTo(filePath.toFile());

        return filename;
    }

    public String saveAvatar(MultipartFile file) throws IOException {
        Path dir = Paths.get(avatarDir).toAbsolutePath();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        file.transferTo(dir.resolve(filename).toFile());
        return filename;
    }

    public void delete(String filename) {
        if (filename == null) return;
        try {
            Files.deleteIfExists(Paths.get(uploadDir, filename));
        } catch (IOException ignored) {}
    }

    public void deleteAvatar(String filename) {
        if (filename == null) return;
        try {
            Files.deleteIfExists(Paths.get(avatarDir, filename));
        } catch (IOException ignored) {}
    }

    public Path resolve(String filename) {
        return Paths.get(uploadDir, filename);
    }

    public Path resolveAvatar(String filename) {
        return Paths.get(avatarDir, filename);
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }
}