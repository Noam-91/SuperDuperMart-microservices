package com.beaconfire.coreservice.domain.complementary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ProductContainer {
    private Long productId;
    private Integer quantity;
}
