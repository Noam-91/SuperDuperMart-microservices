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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@JsonView(Views.Internal.class)
public class Inventory extends ProductContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @Column(name = "product_id")
    private Long productId;

    private Integer quantity;

    @Version
    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;
}
