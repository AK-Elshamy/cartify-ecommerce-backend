package com.elshamy.cartify.service.image;

import com.elshamy.cartify.dtos.ImageDTO;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.Image;
import com.elshamy.cartify.model.Product;
import com.elshamy.cartify.repository.ImageRepository;
import com.elshamy.cartify.repository.ProductRepository;
import com.elshamy.cartify.service.LocalStorageService;
import com.elshamy.cartify.service.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final LocalStorageService localStorageService;


    Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product with id: " + productId + " not found")
        );
    }

    @Override
    public ImageDTO.ImageResponseDTO uploadImage(Long productId, MultipartFile file) {
        Product product = getProductById(productId);

        StoredFile storedFile = localStorageService.store(file);

        Image image = new Image();
        image.setProduct(product);
        image.setFileName(storedFile.fileName());
        image.setFileType(storedFile.fileType());
        image.setFileSize(storedFile.fileSize());

        Image savedImage = imageRepository.save(image);

        return ImageDTO.ImageResponseDTO.from(savedImage);
    }

    @Override
    public List<ImageDTO.ImageResponseDTO> getImagesByProductId(Long productId) {
        getProductById(productId); // ensure product exists
        var images = imageRepository.findByProductId(productId);
        return images.stream().map(ImageDTO.ImageResponseDTO::from).toList();
    }

    @Override
    public void deleteImage(Long imageId) {
        var image = imageRepository.findById(imageId).orElseThrow(
                () -> new ResourceNotFoundException("Image with id: " + imageId + " not found")
        );
        localStorageService.delete(image.getFileName());
        imageRepository.delete(image);
    }

    @Override
    public ImageFile loadImage(Long imageId) {
        var image = imageRepository.findById(imageId).orElseThrow(
                () -> new ResourceNotFoundException("Image with id: " + imageId + " not found")
        );
        ImageFile response = new ImageFile(localStorageService.loadAsResource(image.getFileName()),
                image.getFileType());
        return response;
    }
}