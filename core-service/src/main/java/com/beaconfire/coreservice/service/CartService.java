package com.beaconfire.coreservice.service;

import com.beaconfire.coreservice.dao.CartDao;
import com.beaconfire.coreservice.domain.CartProduct;
import com.beaconfire.coreservice.domain.complementary.Product;

import com.beaconfire.coreservice.dto.Cart;
import com.beaconfire.coreservice.exception.BadRequestException;
import com.beaconfire.coreservice.exception.NotEnoughInventoryException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


@Service
public class CartService {
    private final CartDao cartDao;
    private final AggregatorService aggregatorService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CartService.class);
    public CartService(CartDao cartDao, AggregatorService aggregatorService,
                       InventoryService inventoryService, OrderService orderService,
                       PaymentService paymentService) {
        this.cartDao = cartDao;
        this.aggregatorService = aggregatorService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    /**
     * Get the whole cart by user id
     * use aggregatorService(webclient) to get product info
     * use cartDao to get cart info
     * @return WholeCart
     */
    public Cart getWholeCartByUserId(Long userId) {
        List<CartProduct> cartProducts = cartDao.getAllCartProductByUserId(userId);

        if (cartProducts.isEmpty()) {
            return Cart.builder()
                    .userId(userId)
                    .build();
        }

        List<Product> products = aggregatorService.getProductFromCatalogServiceWithQuantity(cartProducts);

        return Cart.builder()
                .userId(userId)
                .products(products)
                .build();
    }

    @Transactional
    public String purchase(Cart cart, Long updatedBy) throws RuntimeException{
        if(!updatedBy.equals(cart.getUserId())){
            throw new RuntimeException("You cannot buy for others");
        }
        List<Product> products = cart.getProducts();

        // Update inventory and cartProduct
        for(Product product:products){
            if(product.getQuantity()==null){
                logger.error("Purchase quantity is null for product: {}", product.getName());
                throw new RuntimeException("Purchase quantity is null");
            }
            if(product.getQuantity()<=0){
                throw new BadRequestException("Purchase quantity must be greater than 0 ");
            }
            try{
                inventoryService.deductInventory(product.getProductId(), product.getQuantity());
            }catch(NotEnoughInventoryException e){
                throw new NotEnoughInventoryException("Not enough inventory for product: " + product.getName());
            }
            cartDao.deleteCartProduct(cart.getUserId(), product.getProductId());
        }

        // Create a new order
        Long orderId = orderService.createOrder(cart.getUserId(), products);

        // Create a new payment
        BigDecimal totalAmount = products.stream()
                .map(product -> product.getPriceRetail().multiply(new BigDecimal(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String paymentUrl = paymentService.createPayment(orderId, totalAmount);

        return paymentUrl;
    }

    @Transactional
    public void updateProductQuantityInCart(Cart cart, Long updatedBy) throws RuntimeException{
        if(!updatedBy.equals(cart.getUserId())){
            throw new RuntimeException("You cannot change other's cart ");
        }
        List<Product> products = cart.getProducts();
        for(Product product:products){
            if(product.getQuantity()==null){
                logger.error("Purchase quantity is null for product: {}", product.getName());
                throw new RuntimeException("Purchase quantity is null");
            }
            if(product.getQuantity()<=0){
                throw new BadRequestException("Purchase quantity must be greater than 0 ");
            }
            cartDao.update(cart.getUserId(), product.getProductId(), product.getQuantity());
        }
    }

    @Transactional
    public void deleteCartProduct(Long productId, Long userId){
        cartDao.deleteCartProduct(userId, productId);
    }

    /** If exists, add quantity to existing cartProduct
     *  If not exist, add new cartProduct
     *  @param productId
     *  @param userId
     *  @param quantity
     */
    @Transactional
    public void addCartProduct(Long productId, Long userId, Integer quantity){
        if(quantity <= 0){
            throw new BadRequestException("Quantity must be greater than 0");
        }
        Optional<CartProduct> existingCartProduct = cartDao.getCartProductByUserIdAndProductId(userId, productId);
        if(existingCartProduct.isPresent()){
            cartDao.update(userId, productId, existingCartProduct.get().getQuantity() + quantity);
        }else{
            CartProduct newCartProduct = CartProduct.builder()
                    .productId(productId)
                    .userId(userId)
                    .quantity(quantity)
                    .build();
            cartDao.addCartProduct(newCartProduct);
        }
    }
}
