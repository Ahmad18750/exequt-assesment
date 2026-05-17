package com.kira.exequtassesment.controller;

import com.kira.exequtassesment.dto.request.CartItemInfo;
import com.kira.exequtassesment.dto.response.CartResponse;
import com.kira.exequtassesment.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("")
    public ResponseEntity<CartResponse> createCart() {
        CartResponse response = cartService.createCart();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItemToCart(@PathVariable Long cartId, @RequestBody List<CartItemInfo> items) {
        cartService.addItemToCart(cartId, items);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartId}/{itemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
        cartService.removeItemFromCart(cartId, itemId);
        return ResponseEntity.ok().build();
    }
}
