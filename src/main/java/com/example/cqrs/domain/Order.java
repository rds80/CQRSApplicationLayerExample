package com.example.cqrs.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NonNull
    private String customerName;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    @NonNull
    private Integer quantity;

    @Column(nullable = false)
    @NonNull
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    {
        this.createdAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.PENDING;
    }

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
