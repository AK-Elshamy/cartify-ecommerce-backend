package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.ImageDTO;
import com.elshamy.cartify.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final IImageService imageService;

    @PostMapping(
            value = "/products/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ImageDTO.ImageResponseDTO uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return imageService.uploadImage(productId, file);
    }
}
