package com.example.cqrs.application.queries;

import an.awesome.pipelinr.Command;
import com.example.cqrs.application.dto.OrderDto;
import jakarta.validation.constraints.NotNull;

public record GetOrderQuery(
        @NotNull(message = "Order ID is required")
        Long id
) implements Command<OrderDto> {}
