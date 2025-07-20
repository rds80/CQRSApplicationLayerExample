package com.example.cqrs.application.web;

import com.example.cqrs.application.commands.CreateOrderCommand;
import com.example.cqrs.domain.Order;
import com.example.cqrs.domain.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = com.example.cqrs.CqrsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderControllerTest {
    @Autowired
    private  WebApplicationContext context;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void givenOrderNeedsToBeCreated_WhenPostApiToCreateOrderIsCalled_ThenVerifyOrderIsCreated() throws Exception {
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(
                "Alice Cooper",
                "Guitar",
                1,
                BigDecimal.valueOf(1299.99)
        );

        String requestJson = objectMapper.writeValueAsString(createOrderCommand);

        MvcResult mvcResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(request().asyncStarted())
                        .andReturn();


        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice Cooper"))
                .andExpect(jsonPath("$.productName", is("Guitar")))
                .andExpect(jsonPath("$.quantity", is(1)))
                .andExpect(jsonPath("$.price", is(1299.99)));
    }

    @Test
    void givenOrderAlreadyExists_WhenGetApiToGetOrderIsCalled_ThenVerifyOrderIsRetrieved() throws  Exception {
        Order order = Order.builder()
                .customerName("Charlie Brown")
                .productName("Drums")
                .quantity(1)
                .price(BigDecimal.valueOf(899.99))
                .build();

        Order savedOrder = orderRepository.save(order);

        MvcResult mvcResult = mockMvc.perform(get("/api/orders/{id}", savedOrder.getId()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Charlie Brown"));
    }

    @Test
    void givenMultipleOrdersExistForCustomers_WhenGetApiIsCalledToSearchByCustomer_ThenVerifyOrdersForCustomerAreRetrieved() throws Exception {
        Order order1 = Order.builder()
                .customerName("David Bowie")
                .productName("Microphone")
                .quantity(2)
                .price(BigDecimal.valueOf(199.99))
                .build();

        Order order2 = Order.builder()
                .customerName("David Bowie")
                .productName("Amplifier")
                .quantity(1)
                .price(BigDecimal.valueOf(599.99))
                .build();

        orderRepository.saveAndFlush(order1);
        orderRepository.saveAndFlush(order2);

        MvcResult mvcResult = mockMvc.perform(get("/api/orders/customer/{customerName}", "David Bowie"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].customerName", is("David Bowie")))
                .andExpect(jsonPath("$[1].customerName", is("David Bowie")));
    }

    @Test
    void givenInvalidOrderIsCreated_WhenPostApiToCreateOrderIsCalled_ThenVerifyApiReturnsBadRequest() throws  Exception {
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(
                "",
                "Guitar",
                1,
                BigDecimal.valueOf(1299.99)
        );

        String requestJson = objectMapper.writeValueAsString(createOrderCommand);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
    }
}
