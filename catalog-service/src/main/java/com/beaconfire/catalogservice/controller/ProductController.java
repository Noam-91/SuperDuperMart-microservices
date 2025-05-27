package com.beaconfire.catalogservice.controller;

import com.beaconfire.catalogservice.domain.Product;
import com.beaconfire.catalogservice.service.ProductService;
import com.beaconfire.catalogservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getAllProductsInStock() {
        try{
            List<Product> products = productService.getAllProductsInStock();
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("{productId}")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        try{
            return ResponseEntity.ok(productService.getProductById(productId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
