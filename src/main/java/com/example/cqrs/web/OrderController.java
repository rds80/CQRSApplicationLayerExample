package com.example.cqrs.web;

import com.example.cqrs.application.OrderApplicationService;
import com.example.cqrs.application.commands.CreateOrderCommand;
import com.example.cqrs.application.dto.OrderDto;
import com.example.cqrs.application.queries.GetOrderQuery;
import com.example.cqrs.application.queries.GetOrdersByCustomerQuery;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping()
    public CompletableFuture<ResponseEntity<OrderDto>> createOrderAsync(@Valid @RequestBody CreateOrderCommand createOrderCommand) {
        return orderApplicationService.createOrderAsync(createOrderCommand)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<OrderDto>> getOrder(@PathVariable Long id) {
        return orderApplicationService.getOrderAsync(new GetOrderQuery(id))
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerName}")
    public CompletableFuture<ResponseEntity<List<OrderDto>>> getOrdersByCustomer(@PathVariable String customerName) {
        return orderApplicationService.getOrdersByCustomerAsync(new GetOrdersByCustomerQuery(customerName))
                .thenApply(ResponseEntity::ok);
    }
}
