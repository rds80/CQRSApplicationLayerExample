package com.example.cqrs.application;

import an.awesome.pipelinr.Pipeline;
import com.example.cqrs.application.commands.CreateOrderCommand;
import com.example.cqrs.application.dto.OrderDto;
import com.example.cqrs.application.queries.GetOrderQuery;
import com.example.cqrs.application.queries.GetOrdersByCustomerQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderApplicationService {

    private final Pipeline pipeline;

    public OrderApplicationService(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public CompletableFuture<OrderDto> createOrderAsync(CreateOrderCommand createOrderCommand) {
        return CompletableFuture.supplyAsync(() -> pipeline.send(createOrderCommand));
    }

    public CompletableFuture<OrderDto> getOrderAsync(GetOrderQuery getOrderQuery) {
        return CompletableFuture.supplyAsync(() -> pipeline.send(getOrderQuery));
    }

    public CompletableFuture<List<OrderDto>> getOrdersByCustomerAsync(GetOrdersByCustomerQuery getOrdersByCustomerQuery) {
        return CompletableFuture.supplyAsync(() -> pipeline.send(getOrdersByCustomerQuery));
    }
}
