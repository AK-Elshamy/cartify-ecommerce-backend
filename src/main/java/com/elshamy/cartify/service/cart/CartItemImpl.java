package com.elshamy.cartify.service.cart;

import com.elshamy.cartify.dtos.CartItemDTO;
import com.elshamy.cartify.exception.InsufficientStockException;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.Cart;
import com.elshamy.cartify.model.CartItem;
import com.elshamy.cartify.model.Product;
import com.elshamy.cartify.repository.CartItemRepository;
import com.elshamy.cartify.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemImpl implements ICartItem {

    private final CartItemRepository cartItemRepository;
    private final ICart cartService;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartItemDTO.CartItemDetailResponse addItem(
            Long userId,
            CartItemDTO.AddItemRequest request
    ) {
        Cart cart = cartService.getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.productId())
                .map(existingItem -> increaseItemQuantity(existingItem, request.quantity()))
                .orElseGet(() -> createNewCartItem(cart, request));

        CartItem savedItem = cartItemRepository.save(cartItem);
        return CartItemDTO.CartItemDetailResponse.from(savedItem);
    }

    @Override
    @Transactional
    public CartItemDTO.CartItemDetailResponse updateItemQuantity(
            Long userId,
            Long productId,
            CartItemDTO.UpdateQuantityRequest request
    ) {
        CartItem cartItem = findCartItemByUserIdAndProductId(userId, productId);
        validateStock(cartItem.getProduct(), request.quantity());

        cartItem.setQuantity(request.quantity());
        CartItem updatedItem = cartItemRepository.save(cartItem);

        return CartItemDTO.CartItemDetailResponse.from(updatedItem);
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long productId) {
        Cart cart = cartService.getOrCreateCart(userId);
        CartItem cartItem = findCartItem(cart.getId(), productId);
        cart.removeItem(cartItem); // orphanRemoval
    }

    @Override
    @Transactional
    public Page<CartItemDTO.CartItemDetailResponse> getItems(Long userId, Pageable pageable) {
        Cart cart = cartService.getOrCreateCart(userId);

        return cartItemRepository.findByCartId(cart.getId(), pageable)
                .map(CartItemDTO.CartItemDetailResponse::from);
    }

    // =========================================================================
    // Helper Methods (DRY)
    // =========================================================================

    private CartItem findCartItemByUserIdAndProductId(Long userId, Long productId) {
        Cart cart = cartService.getOrCreateCart(userId);
        return findCartItem(cart.getId(), productId);
    }

    private CartItem findCartItem(Long cartId, Long productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with id " + productId + " is not in the cart"
                ));
    }

    private CartItem increaseItemQuantity(CartItem existingItem, int addedQuantity) {
        int newQuantity = existingItem.getQuantity() + addedQuantity;
        validateStock(existingItem.getProduct(), newQuantity);

        existingItem.setQuantity(newQuantity);
        return existingItem;
    }

    private CartItem createNewCartItem(Cart cart, CartItemDTO.AddItemRequest request) {
        Product product = findProductById(request.productId());
        validateStock(product, request.quantity());

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(request.quantity());
        item.setUnitPrice(product.getPrice());
        cart.addItem(item);
        return item;
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getInventory()) {
            throw new InsufficientStockException(
                    String.format(
                            "Insufficient stock for product '%s'. Available: %d, Requested: %d",
                            product.getName(),
                            product.getInventory(),
                            requestedQuantity
                    )
            );
        }
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId
                ));
    }
}