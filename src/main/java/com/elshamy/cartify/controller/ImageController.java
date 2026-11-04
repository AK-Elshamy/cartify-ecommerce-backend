package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.ImageDTO;
import com.elshamy.cartify.service.image.IImageService;
import com.elshamy.cartify.service.image.ImageFile;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Validated
public class ImageController {

    private final IImageService imageService;

    @PostMapping(
            value = "products/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ImageDTO.ImageResponseDTO uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return imageService.uploadImage(productId, file);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ImageDTO.ImageResponseDTO>> getImagesByProductId(@PathVariable @Positive Long productId) {
        return ResponseEntity.ok(imageService.getImagesByProductId(productId));
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
    }

    @GetMapping("/{imageId}/file")
    public ResponseEntity<Resource> getImageFile(@PathVariable @Positive Long imageId) {
        ImageFile imageFile = imageService.loadImage(imageId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageFile.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + imageFile.resource().getFilename() + "\""
                )
                .body(imageFile.resource());
    }


}
