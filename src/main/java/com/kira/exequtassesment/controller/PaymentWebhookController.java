package com.kira.exequtassesment.controller;

import com.kira.exequtassesment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {
    private final PaymentService paymentService;

    @PostMapping("/webhook/{paymentId}")
    public void handlePaymentWebhook(@PathVariable Long paymentId, @RequestBody boolean success) {
        paymentService.handlePaymentWebhook(paymentId, success) ;
    }
}
