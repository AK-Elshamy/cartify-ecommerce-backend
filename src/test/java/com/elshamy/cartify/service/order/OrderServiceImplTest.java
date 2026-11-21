package com.elshamy.cartify.service.order;

import com.elshamy.cartify.dtos.OrderDTO;
import com.elshamy.cartify.exception.InsufficientStockException;
import com.elshamy.cartify.model.*;
import com.elshamy.cartify.repository.AddressRepository;
import com.elshamy.cartify.repository.CartRepository;
import com.elshamy.cartify.repository.OrderRepository;
import com.elshamy.cartify.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Cart cart;
    private Address address;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("sara@cartify.com");

        product = Product.builder()
                .id(10L)
                .name("Clean Code")
                .price(new BigDecimal("500.00"))
                .inventory(10)
                .quantity(5)
                .brand("Prentice Hall")
                .build();

        cart = new Cart();
        cart.setId(1L);
        cart.assignTo(user);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cart.addItem(cartItem);

        address = new Address();
        address.setId(4L);
        address.setStreet("15 El-Geish Street");
        address.setCity("Benha");
        address.setState("Qalyubia");
        address.setPostalCode("13511");
        address.setCountry("Egypt");
        address.setPhoneNumber("01108847637");
        address.setUser(user);
    }

    @Test
    @DisplayName("Should checkout successfully, deduct inventory, and clear cart")
    void checkout_Success() {
        // Arrange
        OrderDTO.CheckoutRequest request = new OrderDTO.CheckoutRequest(4L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(addressRepository.findByIdAndUserId(4L, 1L)).thenReturn(Optional.of(address));
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        // Act
        OrderDTO.OrderResponse response = orderService.checkout(1L, request);

        // Assert
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.totalAmount()).isEqualByComparingTo("1000.00");
        assertThat(response.shippingAddress().getCity()).isEqualTo("Benha");


        assertThat(product.getInventory()).isEqualTo(8); // 10 - 2
        assertThat(product.getQuantity()).isEqualTo(7);  // 5 + 2


        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when checking out with empty cart")
    void checkout_EmptyCart_ThrowsException() {
        // Arrange
        cart.clear();
        OrderDTO.CheckoutRequest request = new OrderDTO.CheckoutRequest(4L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThatThrownBy(() -> orderService.checkout(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty cart");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when requested quantity exceeds inventory")
    void checkout_InsufficientStock_ThrowsException() {
        // Arrange
        product.setInventory(1);
        OrderDTO.CheckoutRequest request = new OrderDTO.CheckoutRequest(4L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(addressRepository.findByIdAndUserId(4L, 1L)).thenReturn(Optional.of(address));
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThatThrownBy(() -> orderService.checkout(1L, request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");

        verify(orderRepository, never()).save(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should cancel pending order and restore product inventory")
    void cancelOrder_Success_RestoresInventory() {
        // Arrange
        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(3);
        orderItem.setUnitPrice(product.getPrice());
        order.addItem(orderItem);

        when(orderRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(order));
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        OrderDTO.OrderResponse response = orderService.cancelOrder(1L, 100L);

        // Assert
        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(product.getInventory()).isEqualTo(13); // 10 + 3
        assertThat(product.getQuantity()).isEqualTo(2);   // 5 - 3
    }

    @Test
    @DisplayName("Should prevent cancelling an already shipped order")
    void cancelOrder_ShippedOrder_ThrowsException() {
        // Arrange
        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.cancelOrder(1L, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot cancel order in status: SHIPPED");

        verify(productRepository, never()).findByIdForUpdate(any());
    }
}