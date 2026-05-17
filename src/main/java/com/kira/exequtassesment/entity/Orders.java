package com.kira.exequtassesment.entity;

import com.kira.exequtassesment.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal totalPrice = BigDecimal.ZERO;
    private OrderStatus orderStatus;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public void transitionTo(OrderStatus status) {
        if (this.orderStatus == null) {
            this.orderStatus = status;
            return;
        }
        if (!orderStatus.canTransitionTo(status)) {
            throw new RuntimeException("Invalid order status transition from " + this.orderStatus + " to " + status);
        }
        this.orderStatus = status;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        this.totalPrice = this.totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    }
}
