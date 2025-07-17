package com.example.cqrs.application.handlers;

import an.awesome.pipelinr.Command;
import com.example.cqrs.application.commands.CreateOrderCommand;
import com.example.cqrs.application.dto.OrderDto;
import com.example.cqrs.domain.Order;
import com.example.cqrs.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateOrderCommandHandler implements Command.Handler<CreateOrderCommand, OrderDto> {

    private final OrderRepository orderRepository;

    @Override
    public OrderDto handle(CreateOrderCommand createOrderCommand) {
        Order order = Order.builder()
                .customerName(createOrderCommand.customerName())
                .productName(createOrderCommand.productName())
                .quantity(createOrderCommand.quantity())
                .price(createOrderCommand.price())
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        return OrderDto.builder()
                .id(savedOrder.getId())
                .customerName(savedOrder.getCustomerName())
                .productName(savedOrder.getProductName())
                .quantity(savedOrder.getQuantity())
                .price(savedOrder.getPrice())
                .totalPrice(savedOrder.getTotalPrice())
                .status(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }
}
