package com.elshamy.cartify.service.image;

import com.elshamy.cartify.dtos.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {

    ImageDTO.ImageResponseDTO uploadImage(Long productId, MultipartFile file);

    List<ImageDTO.ImageResponseDTO> getImagesByProductId(Long productId);

    void deleteImage(Long imageId);
}