package com.beaconfire.catalogservice.controller;

import com.beaconfire.catalogservice.domain.Category;
import com.beaconfire.catalogservice.domain.Product;
import com.beaconfire.catalogservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/products")
public class ProductInternalController {
    private final ProductService productService;
    public ProductInternalController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/allInStock")
    public ResponseEntity<?> getAllProductsInStock() {
        try{
            List<Product> products = productService.getAllProductsInStock();
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/allCategories")
    public ResponseEntity<?> getAllCategories() {
        try{
            List<Category> categories = productService.getAllCategories();
            return ResponseEntity.ok(categories);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        try{
            return ResponseEntity.ok(productService.getProductById(productId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts(@RequestHeader("X-User-Role") String userRole) {
        try{
            List<Product> products = productService.getAllProducts(userRole);
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/update/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestBody Product product,
                                           @RequestHeader(name="X-User-Id") Long userId,
                                           @RequestHeader("X-User-Role") String userRole
    ) {
        try{
            Product newProduct = productService.updateProduct(productId, product, userId, userRole);
            return ResponseEntity.ok(newProduct);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/batch")
    public ResponseEntity<?> getProductsByIds(@RequestParam Long[] productIds) {
        try{
            List<Product> products = productService.getBatchProductsById(List.of(productIds));
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody Product product,
                                           @RequestHeader(name="X-User-Id") Long userId,
                                           @RequestHeader("X-User-Role") String userRole) {
        try{
            Product newProduct = productService.createProduct(product, userId, userRole);
            return ResponseEntity.ok(newProduct);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
