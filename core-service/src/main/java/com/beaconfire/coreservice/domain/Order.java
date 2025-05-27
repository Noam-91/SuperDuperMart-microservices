package com.beaconfire.coreservice.domain;

import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = "orderProducts")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ordertable")
@JsonView(Views.Public.class)
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    public Long orderId;

    @Column(name = "user_id")
    public Long userId;

    @Column(insertable = false)
    private String status;          // "CANCELED", "COMPLETED", "PROCESSING"

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Version
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;

    @Column(name = "updated_by")
    @JsonView(Views.Internal.class)
    private Long updatedBy;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    @Builder.Default
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Transient
    private Double totalPrice;

    @Transient
    private Integer totalQuantity;
}
