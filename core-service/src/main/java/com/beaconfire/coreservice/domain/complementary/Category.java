package com.beaconfire.coreservice.domain.complementary;

import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long categoryId;
    private String name;
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
}
