package com.kira.exequtassesment;

import com.kira.exequtassesment.entity.Orders;
import com.kira.exequtassesment.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.kira.exequtassesment.enums.OrderStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class OrderStateMachineTests {

    // Test valid state transitions
    @Test
    void createdToPaymentPending() {
        var order = new Orders();
        order.transitionTo(OrderStatus.CREATED);

        order.transitionTo(PENDING_PAYMENT);
        assertThat(order.getOrderStatus()).isEqualTo(PENDING_PAYMENT);
    }

    @Test
    void createdToCancelled() {
        var order = new Orders();
        order.transitionTo(OrderStatus.CREATED);
        order.transitionTo(CANCELLED);
        assertThat(order.getOrderStatus()).isEqualTo(CANCELLED);
    }

    @Test
    void pendingPaymentToPaid() {
        var order = new Orders();
        order.transitionTo(OrderStatus.CREATED);
        order.transitionTo(PENDING_PAYMENT);

        order.transitionTo(PAID);
        assertThat(order.getOrderStatus()).isEqualTo(PAID);
    }

    @Test
    void pendingPaymentToFailed() {
        var order = new Orders();
        order.transitionTo(OrderStatus.CREATED);
        order.transitionTo(PENDING_PAYMENT);
        order.transitionTo(PAYMENT_FAILED);
        assertThat(order.getOrderStatus()).isEqualTo(PAYMENT_FAILED);
    }

    // Test invalid state transitions
    @Test
    void invalidTransitionFromCreated() {
        var order = new Orders();
        order.transitionTo(OrderStatus.CREATED);
        try {
            order.transitionTo(PAID);
        } catch (RuntimeException e) {
            assertThat(order.getOrderStatus()).isNotEqualTo(PAID);
        }
        try {
            order.transitionTo(REFUNDED);
        } catch (RuntimeException e) {
            assertThat(order.getOrderStatus()).isNotEqualTo(REFUNDED);
        }
        try {
            order.transitionTo(PAYMENT_FAILED);
        } catch (RuntimeException e) {
            assertThat(order.getOrderStatus()).isNotEqualTo(PAYMENT_FAILED);
        }

        order.transitionTo(OrderStatus.PENDING_PAYMENT);
        try {
            order.transitionTo(CREATED);
        } catch (RuntimeException e) {
            assertThat(order.getOrderStatus()).isNotEqualTo(CREATED);
        }
        try {
            order.transitionTo(REFUNDED);
        } catch (RuntimeException e) {
            assertThat(order.getOrderStatus()).isNotEqualTo(REFUNDED);
        }
        try {
            order.transitionTo(PAYMENT_FAILED);
        } catch (RuntimeException e) {
            assertThat(order.getOrderStatus()).isNotEqualTo(PAYMENT_FAILED);
        }
    }

}
