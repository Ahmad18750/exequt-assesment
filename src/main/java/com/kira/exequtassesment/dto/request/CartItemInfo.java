package com.kira.exequtassesment.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemInfo {
    private Long productId;
    private BigDecimal price;
    private int quantity;
}
