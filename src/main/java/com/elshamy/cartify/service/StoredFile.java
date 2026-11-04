package com.elshamy.cartify.service;

public record StoredFile(
        String fileName,
        String fileType,
        long fileSize,
        String storagePath
) {
}