package com.example.cqrs.application.commands;

import an.awesome.pipelinr.Command;
import com.example.cqrs.application.dto.OrderDto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderCommand(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotBlank(message = "Product name is required")
        String productName,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price
) implements Command<OrderDto> {
}
