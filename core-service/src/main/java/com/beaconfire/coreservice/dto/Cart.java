package com.beaconfire.coreservice.dto;

import com.beaconfire.coreservice.domain.complementary.Product;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonView(Views.Public.class)

public class Cart {
    private Long userId;
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
