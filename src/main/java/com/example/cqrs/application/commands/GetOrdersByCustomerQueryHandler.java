package com.example.cqrs.application.commands;

import an.awesome.pipelinr.Command;
import com.example.cqrs.application.dto.OrderDto;
import com.example.cqrs.application.queries.GetOrdersByCustomerQuery;
import com.example.cqrs.domain.Order;
import com.example.cqrs.domain.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetOrdersByCustomerQueryHandler implements Command.Handler<GetOrdersByCustomerQuery, List<OrderDto>> {

    private final OrderRepository orderRepository;

    public GetOrdersByCustomerQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderDto> handle(GetOrdersByCustomerQuery getOrdersByCustomerQuery) {
        List<Order> orders = orderRepository.findByCustomerName(getOrdersByCustomerQuery.customerName());

        return orders.stream()
                .map(order -> OrderDto.builder()
                        .id(order.getId())
                        .customerName(order.getCustomerName())
                        .productName(order.getProductName())
                        .quantity(order.getQuantity())
                        .price(order.getPrice())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getOrderStatus())
                        .createdAt(order.getCreatedAt())
                        .build())
                .toList();
    }
}
