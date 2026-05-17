package com.kira.exequtassesment.service;

import com.kira.exequtassesment.entity.*;
import com.kira.exequtassesment.enums.CartStatus;
import com.kira.exequtassesment.enums.OrderStatus;
import com.kira.exequtassesment.repository.CartRepository;
import com.kira.exequtassesment.repository.OrderItemRepository;
import com.kira.exequtassesment.repository.OrderRepository;
import com.kira.exequtassesment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void checkoutCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        if (cart.getStatus() == CartStatus.CHECKED_OUT) {
            throw new RuntimeException("Cart is already checked out");
        }

        Map<Long, Product> cartProducts = getCartProducts(cart);

        Orders order = new Orders();
        order.transitionTo(OrderStatus.CREATED);
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartProducts.get(cartItem.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.addItem(orderItem);
        }

        cart.setStatus(CartStatus.CHECKED_OUT);

        cartRepository.save(cart);
        orderRepository.save(order);
    }

    public void cancelOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getOrderStatus() == OrderStatus.CREATED) {
            order.transitionTo(OrderStatus.CANCELLED);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Order cannot be cancelled");
        }
    }

    private Map<Long, Product> getCartProducts(Cart cart) {
        List<Long> productIds = cart.getItems()
                .stream()
                .map(CartItem::getProductId)
                .toList();
        return productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
