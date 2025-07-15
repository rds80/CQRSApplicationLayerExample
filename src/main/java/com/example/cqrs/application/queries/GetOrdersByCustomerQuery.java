package com.example.cqrs.application.queries;

import an.awesome.pipelinr.Command;
import com.example.cqrs.application.dto.OrderDto;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record GetOrdersByCustomerQuery(
        @NotBlank(message = "Customer name is required")
        String customerName
) implements Command<List<OrderDto>> {
}
