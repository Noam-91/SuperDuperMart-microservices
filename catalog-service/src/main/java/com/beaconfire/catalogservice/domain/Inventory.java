package com.beaconfire.catalogservice.domain;

import com.beaconfire.catalogservice.view.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonView(Views.Public.class)
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id", insertable = false, updatable = false)
    private Long inventoryId;

    private Integer quantity;

    @Version
    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;

    @OneToOne
//    @JsonIgnore
    @JsonBackReference
    @JoinColumn(name = "product_id")
    private Product product;
}
