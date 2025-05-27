package com.beaconfire.coreservice.domain.complementary;

import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonView(Views.Internal.class)
public class Stats {
    private Double profit;
    private Integer sales;
}
