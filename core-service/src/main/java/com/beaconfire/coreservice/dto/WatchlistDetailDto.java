package com.beaconfire.coreservice.dto;

import com.beaconfire.coreservice.domain.complementary.Product;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Column;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonView(Views.Public.class)
public class WatchlistDetailDto {
    private Long watchlistId;
    private Long userId;
    private String name;
    @JsonView(Views.Internal.class)
    private Boolean isActive;
    private Timestamp updatedAt;
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
