package com.kira.exequtassesment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@RequiredArgsConstructor
public class Product {

    @Id
    private Long id;

    private String name;
    private BigDecimal price;
    private Long stock;
}
