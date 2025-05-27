package com.beaconfire.catalogservice.domain;

import com.beaconfire.catalogservice.view.Views;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonView(Views.Public.class)
@ToString(exclude = {"inventory","category"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", insertable = false, updatable = false)
    private Long productId;

    private String name;

    private String description;

    @Column(name = "price_retail")
    private BigDecimal priceRetail;

    @Column(name = "price_wholesale")
    @JsonView(Views.Internal.class)
    private BigDecimal priceWholesale;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active", insertable = false)
    @JsonView(Views.Internal.class)
    private Boolean isActive;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonView(Views.Internal.class)
    private Timestamp createdAt;

    @Version
    @Column(name = "updated_at", insertable = false, updatable = false)
    @JsonView(Views.Internal.class)
    private Timestamp updatedAt;

    @Column(name = "created_by")
    @JsonView(Views.Internal.class)
    private Long createdBy;

    @Column(name = "updated_by")
    @JsonView(Views.Internal.class)
    private Long updatedBy;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Inventory inventory;
}
