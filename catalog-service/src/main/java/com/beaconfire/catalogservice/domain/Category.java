package com.beaconfire.catalogservice.domain;

import com.beaconfire.catalogservice.view.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.Public.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", insertable = false, updatable = false)
    private Long categoryId;

    private String name;

    @Column(name = "is_active")
    @JsonView(Views.Internal.class)
    private Boolean isActive;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonView(Views.Internal.class)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @JsonView(Views.Internal.class)
    private Timestamp updatedAt;

    @Column(name = "created_by")
    @JsonView(Views.Internal.class)
    private Long createdBy;

    @Column(name = "updated_by")
    @JsonView(Views.Internal.class)
    private Long updatedBy;

    @OneToMany(mappedBy = "category", cascade = CascadeType.MERGE)
    @JsonIgnore
    private List<Product> products;
}
