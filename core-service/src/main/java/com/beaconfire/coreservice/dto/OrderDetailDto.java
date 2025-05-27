package com.beaconfire.coreservice.dto;


import com.beaconfire.coreservice.domain.complementary.Product;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonView(Views.Public.class)
public class OrderDetailDto {
    public Long orderId;
    public Long userId;
    private String status;          // "CANCELLED", "COMPLETED", "PROCESSING"
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<Product> products;
    private Double totalPrice;
    private Integer totalQuantity;
}
