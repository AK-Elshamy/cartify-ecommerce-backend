package com.elshamy.cartify.dtos;


import com.elshamy.cartify.model.Image;

public class ImageDTO {

    public record ImageResponseDTO(
            Long id,
            String fileName,
            String fileType,
            Long fileSize,
            String downloadUrl
    ) {
        public static ImageResponseDTO from(Image image) {
            return new ImageResponseDTO(
                    image.getId(), image.getFileName(), image.getFileType(), image.getFileSize(), image.getDownloadUrl()
            );
        }
    }
}