package com.beaconfire.coreservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(insertable = false)
    private String status;              // "DECLINED", "SUCCESS", "PENDING"

    private String type;

    private BigDecimal total;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Version
    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;
}
