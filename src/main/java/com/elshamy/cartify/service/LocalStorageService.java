package com.elshamy.cartify.service;

import com.elshamy.cartify.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalStorageService {

    private final Path uploadDirectory;
    private final long maxFileSize;

    public LocalStorageService(
            @Value("${app.upload.dir}") String uploadDir,
            @Value("${app.upload.max-file-size}") String maxFileSize
    ) {
        this.uploadDirectory = Path.of(uploadDir)
                .toAbsolutePath()
                .normalize();

        this.maxFileSize = parseSize(maxFileSize);
    }

    public StoredFile store(MultipartFile file) {
        validateFile(file);

        String fileType = file.getContentType();
        String extension = getExtension(fileType);

        String fileName = UUID.randomUUID().toString().substring(7).toUpperCase() + extension;
        Path destination = uploadDirectory.resolve(fileName).normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        try {
            Files.createDirectories(uploadDirectory);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination);
            }

            return new StoredFile(
                    fileName,
                    fileType,
                    file.getSize(),
                    destination.toString()
            );

        } catch (IOException exception) {
            throw new RuntimeException("Could not store file", exception);
        }
    }

    public void delete(String fileName) {
        Path filePath = uploadDirectory.resolve(fileName).normalize();

        if (!filePath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new RuntimeException("Could not delete file", exception);
        }
    }

    public Resource loadAsResource(String fileName) {
        Path filePath = uploadDirectory.resolve(fileName).normalize();

        if (!filePath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        if (!Files.exists(filePath)) {
            throw new StorageFileNotFoundException("File not found in storage" + fileName);
        }
        return new FileSystemResource(filePath);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds the limit");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/webp"))) {
            throw new IllegalArgumentException("Unsupported image type");
        }
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Unsupported image type");
        };
    }

    private long parseSize(String size) {
        if (size.endsWith("MB")) {
            return Long.parseLong(size.replace("MB", "")) * 1024 * 1024;
        }

        if (size.endsWith("KB")) {
            return Long.parseLong(size.replace("KB", "")) * 1024;
        }

        return Long.parseLong(size);
    }
}