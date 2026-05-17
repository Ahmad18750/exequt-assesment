package com.kira.exequtassesment.enums;

import java.util.Map;
import java.util.Set;


public enum OrderStatus {
    CREATED,
    PENDING_PAYMENT,
    PAYMENT_FAILED,
    PAID,
    CANCELLED,
    REFUNDED;

    private static final Map<OrderStatus, Set<OrderStatus>> orderTransitionStateMachine = Map.of(
            CREATED,         Set.of(PENDING_PAYMENT, CANCELLED),
            PENDING_PAYMENT, Set.of(PAYMENT_FAILED, PAID),
            PAID,            Set.of(REFUNDED),
            PAYMENT_FAILED,  Set.of(PENDING_PAYMENT),
            CANCELLED,       Set.of(),
            REFUNDED,        Set.of()
    );

    public boolean canTransitionTo(OrderStatus status) {
        return orderTransitionStateMachine.get(this).contains(status);
    }
}
