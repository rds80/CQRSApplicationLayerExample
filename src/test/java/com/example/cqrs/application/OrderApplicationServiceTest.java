package com.example.cqrs.application;


import com.example.cqrs.application.commands.CreateOrderCommand;
import com.example.cqrs.application.dto.OrderDto;
import com.example.cqrs.domain.OrderRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;


@SpringBootTest(classes = com.example.cqrs.CqrsApplication.class)
@ActiveProfiles("test")
@Transactional

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

    }


}
