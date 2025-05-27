package com.beaconfire.coreservice.domain.complementary;

import com.beaconfire.coreservice.domain.Inventory;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonView(Views.Public.class)
public class Product {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal priceRetail;

    @JsonView(Views.Internal.class)
    private BigDecimal priceWholesale;

    private String imageUrl;
    @JsonView(Views.Internal.class)
    private Boolean isActive;
    @JsonView(Views.Internal.class)
    private Timestamp createdAt;
    @JsonView(Views.Internal.class)
    private Timestamp updatedAt;
    @JsonView(Views.Internal.class)
    private Long createdBy;
    @JsonView(Views.Internal.class)
    private Long updatedBy;
    private Category category;

    @JsonView(Views.Internal.class)
    private Inventory inventory;

    @JsonView(Views.Internal.class)
    private Stats stats;

    private Integer quantity;
}
