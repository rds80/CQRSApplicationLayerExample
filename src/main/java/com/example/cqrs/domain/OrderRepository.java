package com.example.cqrs.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerName(String customerName);
    List<Order> findByStatus(OrderStatus orderStatus);
}
