package com.kira.exequtassesment.service;

import com.kira.exequtassesment.entity.Orders;
import com.kira.exequtassesment.entity.Payment;
import com.kira.exequtassesment.enums.OrderStatus;
import com.kira.exequtassesment.enums.PaymentStatus;
import com.kira.exequtassesment.repository.OrderRepository;
import com.kira.exequtassesment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kira.exequtassesment.enums.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void initPayment(Long orderId) {
        Orders order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus == PENDING_PAYMENT || currentStatus == PAID)
            return;

        if (currentStatus != CREATED && currentStatus != PAYMENT_FAILED)
            throw new RuntimeException("Order is not in a valid state for payment initiation");

        if (!currentStatus.canTransitionTo(PENDING_PAYMENT))
            throw new RuntimeException("Invalid order status transition");

        if (paymentRepository.findByOrderIdAndPaymentStatus(orderId, PaymentStatus.PENDING).isPresent())
            throw new RuntimeException("Payment is already pending for this order");

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        order.transitionTo(PENDING_PAYMENT);
        orderRepository.save(order);
    }

    @Transactional
    public void handlePaymentWebhook(Long paymentId, boolean success) {
        Payment payment = paymentRepository.findByIdWithLock(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not in a pending state");
        }

        Orders order = orderRepository.findByIdWithLock(payment.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (success) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            order.transitionTo(PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            order.transitionTo(PAYMENT_FAILED);
        }
        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}
