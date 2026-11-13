package com.elshamy.cartify.service.cart;

import com.elshamy.cartify.dtos.CartDTO;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.Cart;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.repository.CartRepository;
import com.elshamy.cartify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CartImpl implements ICart {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Cart getOrCreateCart(Long userId) {

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found with id: " + userId
                                    )
                            );

                    Cart cart = new Cart();
                    cart.assignTo(user);

                    return cartRepository.save(cart);
                });
    }

    @Override
    @Transactional
    public CartDTO.CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return CartDTO.CartResponse.from(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId)
                .ifPresent(Cart::clear);
    }
}
