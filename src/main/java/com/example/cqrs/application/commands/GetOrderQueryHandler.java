package com.example.cqrs.application.commands;

import an.awesome.pipelinr.Command;
import com.example.cqrs.application.dto.OrderDto;
import com.example.cqrs.application.queries.GetOrderQuery;
import com.example.cqrs.domain.Order;
import com.example.cqrs.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetOrderQueryHandler implements Command.Handler<GetOrderQuery, OrderDto> {

    private final OrderRepository orderRepository;

    @Override
    public OrderDto handle(GetOrderQuery getOrderQuery) {
        Order order = orderRepository.findById(getOrderQuery.id())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with: " + getOrderQuery.id()));

        return OrderDto.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
