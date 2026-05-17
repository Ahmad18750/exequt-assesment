package com.kira.exequtassesment.controller;

import com.kira.exequtassesment.service.OrderService;
import com.kira.exequtassesment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<?> checkoutCart(@PathVariable Long cartId) {
        orderService.checkoutCart(cartId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/payment/start")
    public ResponseEntity<?> startPayment(@PathVariable Long orderId) {
        paymentService.initPayment(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
