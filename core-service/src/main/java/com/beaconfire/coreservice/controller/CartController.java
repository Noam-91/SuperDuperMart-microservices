package com.beaconfire.coreservice.controller;

import com.beaconfire.coreservice.dto.Cart;
import com.beaconfire.coreservice.exception.NotEnoughInventoryException;
import com.beaconfire.coreservice.service.CartService;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getCart(@RequestHeader("X-User-Id") Long userId) {
        Cart cart = cartService.getWholeCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/purchase")
    @JsonView(Views.Public.class)
    public ResponseEntity<Map<String,String>> purchase(@RequestBody Cart cart,
                                      @RequestHeader("X-User-Id") Long userId) {
        try{
            String paymentUrl = cartService.purchase(cart, userId);
            return ResponseEntity.ok(Map.of("paymentUrl",paymentUrl));
        }catch (NotEnoughInventoryException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/update")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> updateProductQuantityInCart(@RequestBody Cart cart,
                                           @RequestHeader("X-User-Id") Long userId) {
        cartService.updateProductQuantityInCart(cart, userId);
        Cart newCart = cartService.getWholeCartByUserId(userId);
        return ResponseEntity.ok(newCart);
    }

    @PostMapping("/add")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> addProduct(@RequestParam(name = "productId") Long productId,
                                        @RequestParam(name = "quantity", defaultValue = "1") Integer quantity,
                                        @RequestHeader("X-User-Id") Long userId) {
        cartService.addCartProduct(productId, userId, quantity);
        Cart newCart = cartService.getWholeCartByUserId(userId);
        return ResponseEntity.ok(newCart);

    }

    @DeleteMapping("/remove")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> removeProduct(@RequestParam(name = "productId") Long productId,
                                           @RequestHeader("X-User-Id") Long userId) {
        cartService.deleteCartProduct(productId, userId);
        Cart newCart = cartService.getWholeCartByUserId(userId);
        return ResponseEntity.ok(newCart);
    }
}
