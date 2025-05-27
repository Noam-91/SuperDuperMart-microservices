package com.beaconfire.coreservice.domain;

import com.beaconfire.coreservice.domain.complementary.ProductContainer;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProduct extends ProductContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderproduct_id")
    private Long orderProductId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "price_retail_at_purchase", precision = 10, scale = 2)
    private BigDecimal priceRetailAtPurchase;

    @Column(name = "price_wholesale_at_purchase", precision = 10, scale = 2)
    @JsonView(Views.Internal.class)
    private BigDecimal priceWholesaleAtPurchase;

    private Integer quantity;

    @Version
    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;
}
