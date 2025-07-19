package com.example.cqrs.application;


import com.example.cqrs.application.commands.CreateOrderCommand;
import com.example.cqrs.application.dto.OrderDto;
import com.example.cqrs.application.queries.GetOrderQuery;
import com.example.cqrs.application.queries.GetOrdersByCustomerQuery;
import com.example.cqrs.domain.Order;
import com.example.cqrs.domain.OrderRepository;
import com.example.cqrs.domain.OrderStatus;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;


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
        var currentDateTime =LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

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
        assertThat(result.createdAt()).isEqualTo(currentDateTime);
    }

    @Test
    void getOrdersByCustomerAsync_ShouldRetrieveCustomerOrders() throws Exception {
        var currentDateTime =LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        Order order1 = Order.builder()
                .status(OrderStatus.SHIPPED)
                .customerName("Bob Johnson")
                .productName("Tablet")
                .quantity(1)
                .price(BigDecimal.valueOf(299.99))
                .createdAt(currentDateTime)
                .build();

        Order order2 = Order.builder()
                .status(OrderStatus.SHIPPED)
                .customerName("Bob Johnson")
                .productName("Keyboard")
                .quantity(1)
                .price(BigDecimal.valueOf(79.99))
                .createdAt(currentDateTime)
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        GetOrdersByCustomerQuery query = new GetOrdersByCustomerQuery("Bob Johnson");

        // When
        CompletableFuture<List<OrderDto>> future = orderApplicationService.getOrdersByCustomerAsync(query);
        List<OrderDto> results = future.get();

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(order -> order.customerName().equals("Bob Johnson"));
        assertThat(results).extracting(OrderDto::productName)
                .containsExactlyInAnyOrder("Tablet", "Keyboard");
    }

    @Test
    void givenOrderIdDoesNotExist_WhenGeOrderQueryHandlerCalled_ThenVerifyErrorMessage() {
        // Given
        GetOrderQuery query = new GetOrderQuery(999L);

        // When & Then
        CompletableFuture<OrderDto> future = orderApplicationService.getOrderAsync(query);
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        Throwable cause = exception.getCause();
        String errorMessage = cause.getMessage();
        assertThat(errorMessage).isEqualTo("Order not found with: 999");

    }


    @Test
    void givenOrderCreationHasException_WhenCreateOrderCommandCalled_ThenVerifyExceptionIsThrown() {
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .customerName("")
                .productName("Product")
                .quantity(0)
                .price(BigDecimal.valueOf(-1))
                .build();

        CompletableFuture<OrderDto> future = orderApplicationService.createOrderAsync(createOrderCommand);
        // Capture the exception
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);

        // Check the actual cause (the business exception)
        Throwable cause = exception.getCause();
        assertThat(cause).isInstanceOf(ConstraintViolationException.class);
        assertThat(cause.getMessage()).contains("Customer name cannot be empty");
    }

    @Test
    void givenOrderIsCreated_WhenCreateOrderCommandIsCalled_VerifyOrderIsPersistedToDatabase() throws Exception {
        // Given
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .customerName("Alice Cooper")
                .productName("Guitar")
                .quantity(1)
                .price(BigDecimal.valueOf(1299.99))
                .build();

        long initialCount = orderRepository.count();

        // When
        CompletableFuture<OrderDto> future = orderApplicationService.createOrderAsync(createOrderCommand);
        OrderDto result = future.get();

        // Then
        assertThat(orderRepository.count()).isEqualTo(initialCount + 1);

        Order persistedOrder = orderRepository.findById(result.id()).orElseThrow();
        assertThat(persistedOrder.getCustomerName()).isEqualTo("Alice Cooper");
        assertThat(persistedOrder.getProductName()).isEqualTo("Guitar");
        assertThat(persistedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void givenCustomerDoesNotExistInDatabase_WhengetOrdersByCustomerIsCalled_ThenVerifyEmptyListIsReturned() throws Exception {
        GetOrdersByCustomerQuery getOrdersByCustomerQuery = new GetOrdersByCustomerQuery("NonExistentCustomer");

        CompletableFuture<List<OrderDto>> future = orderApplicationService.getOrdersByCustomerAsync(getOrdersByCustomerQuery);
        var results = future.get();

        assertThat(results).isEmpty();
    }
}
