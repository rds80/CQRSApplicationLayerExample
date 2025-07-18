package com.example.cqrs.application;


import com.example.cqrs.application.commands.CreateOrderCommand;
import com.example.cqrs.application.dto.OrderDto;
import com.example.cqrs.application.queries.GetOrderQuery;
import com.example.cqrs.domain.Order;
import com.example.cqrs.domain.OrderRepository;
import com.example.cqrs.domain.OrderStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;


@SpringBootTest(classes = com.example.cqrs.CqrsApplication.class)
@ActiveProfiles("test")

public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderRepository orderRepository;


    @Test
    void givenOrderNeedsToBeCreated_WhenServiceIsCalled_ThenVerifyOrderIsCreated() throws ExecutionException, InterruptedException {
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(
                "John Doe",
                "Laptop",
                2,
                BigDecimal.valueOf(999.99)
        );

        CompletableFuture<OrderDto> future = orderApplicationService.createOrderAsync(createOrderCommand);
        OrderDto result = future.get();

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.customerName()).isEqualTo("John Doe");
        assertThat(result.productName()).isEqualTo("Laptop");
        assertThat(result.quantity()).isEqualTo(2);
        assertThat(result.price()).isEqualTo(BigDecimal.valueOf(999.99));
        assertThat(result.totalPrice()).isEqualTo(BigDecimal.valueOf(1999.98));
        assertThat(result.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.createdAt()).isNotNull();
    }

    @Test
    void givenOrderIsAlreadyCreated_WhengetOrderAsyncIsCalled_ThenVerifyOrderIsRetrievedSuccessfully() throws ExecutionException, InterruptedException {
        var currentDateTime = LocalDateTime.now();

        Order order = Order.builder()
                .status(OrderStatus.SHIPPED)
                .customerName("Jane Smith")
                .productName("Phone")
                .quantity(1)
                .price(BigDecimal.valueOf(599.99))
                .createdAt(currentDateTime)
                .build();

        Order savedOrder = orderRepository.saveAndFlush(order);
        System.out.println("Saved order ID: " + savedOrder.getId());
        System.out.println("Total orders in DB: " + orderRepository.count());

        Optional<Order> verification = orderRepository.findById(savedOrder.getId());
        System.out.println("Order exists in DB: " + verification.isPresent());

        GetOrderQuery getOrderQuery = new GetOrderQuery(savedOrder.getId());

        CompletableFuture<OrderDto> future = orderApplicationService.getOrderAsync(getOrderQuery);
        OrderDto result = future.get();

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(savedOrder.getId());
        assertThat(result.status()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(result.customerName()).isEqualTo("Jane Smith");
        assertThat(result.productName()).isEqualTo("Phone");
        assertThat(result.quantity()).isEqualTo(1);
        assertThat(result.price()).isEqualTo(BigDecimal.valueOf(599.99));
    }
}
