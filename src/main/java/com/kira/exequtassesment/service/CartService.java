package com.kira.exequtassesment.service;

import com.kira.exequtassesment.dto.request.CartItemInfo;
import com.kira.exequtassesment.dto.response.CartResponse;
import com.kira.exequtassesment.entity.Cart;
import com.kira.exequtassesment.entity.CartItem;
import com.kira.exequtassesment.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    // create a new empty cart and return the cart id
    public CartResponse createCart() {
        Cart cart = new Cart();
        Long cartId = cartRepository.saveAndFlush(cart).getId();
        return new CartResponse(cartId);
    }

    // add items to the cart
    public void addItemToCart(Long cartId, List<CartItemInfo> items) {
        Cart cart = getCart(cartId);
        for (CartItemInfo item : items) {
            addItem(cart, item.getProductId(), item.getQuantity(), item.getPrice());
        }
        cartRepository.save(cart);
    }

    // remove an item from the cart
    public void removeItemFromCart(Long cartId, Long itemId) {
        Cart cart = getCart(cartId);
        cart.getItems()
                .stream()
                .filter(i -> i.getId().equals(itemId)).findFirst()
                .ifPresent(cart.getItems()::remove);
        cartRepository.save(cart);
    }

    private Cart getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    private void addItem(Cart cart, Long productId, int quantity, BigDecimal price) {
        Optional<CartItem> itemOptional = cart.getItems()
            .stream()
            .filter(i -> i.getProductId().equals(productId))
            .findFirst();
        if (itemOptional.isEmpty()) {
           var item = new CartItem(productId, quantity, price);
           cart.getItems().add(item);
        } else {
            var item = itemOptional.get();
            item.setQuantity(quantity);
            item.setPrice(price);
        }
    }
}
