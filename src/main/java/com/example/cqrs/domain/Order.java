package com.example.cqrs.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name ="orders")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
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
    private OrderStatus status;

    {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
