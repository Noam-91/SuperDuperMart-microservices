package com.beaconfire.coreservice.domain;

import com.beaconfire.coreservice.domain.complementary.ProductContainer;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cartproduct")
@JsonView(Views.Public.class)
public class CartProduct extends ProductContainer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    private Integer quantity;

    @Version
    @Column(name = "updated_at", insertable = false)
    @JsonView(Views.Internal.class)
    private Timestamp updatedAt;
}
