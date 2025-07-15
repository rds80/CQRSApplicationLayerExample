package com.example.cqrs.application.dto;

import com.example.cqrs.domain.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
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
