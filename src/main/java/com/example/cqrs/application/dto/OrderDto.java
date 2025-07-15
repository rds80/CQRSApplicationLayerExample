package com.example.cqrs.application.dto;

import com.example.cqrs.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDto(
        Long id,
        String customerName,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
