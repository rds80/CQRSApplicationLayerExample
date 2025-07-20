package com.example.cqrs.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderTest {

    @Test
    void givenOrderHasValidData_whenCalculatingTotalPrice_TheReturnCorrectAmount() {
        Order order = Order.builder()
                .customerName("Jane Smith")
                .productName("Phone")
                .quantity(3)
                .price(BigDecimal.valueOf(299.99))
                .build();

       var totalPrice = order.getTotalPrice();

       assertThat(totalPrice).isEqualTo(BigDecimal.valueOf(899.97));
    }
}
