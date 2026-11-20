package com.elshamy.cartify.service.wishlist;

import com.elshamy.cartify.dtos.ProductDTO;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.Product;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.repository.ProductRepository;
import com.elshamy.cartify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements IWishlistService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductDTO.ProductSummaryResponse addToWishlist(Long userId, Long productId) {
        User user = findUserById(userId);
        Product product = findProductById(productId);


        user.addToWishlist(product);
        userRepository.save(user);

        return ProductDTO.ProductSummaryResponse.from(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO.ProductSummaryResponse> getWishlist(Long userId, Pageable pageable) {
        return productRepository.findWishlistByUserId(userId, pageable)
                .map(ProductDTO.ProductSummaryResponse::from);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        User user = findUserById(userId);
        Product product = findProductById(productId);

        if (!user.getWishlist().contains(product)) {
            throw new ResourceNotFoundException("Product not found in wishlist with id: " + productId);
        }

        user.removeFromWishlist(product);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void clearWishlist(Long userId) {
        User user = findUserById(userId);
        user.clearWishlist();
        userRepository.save(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}