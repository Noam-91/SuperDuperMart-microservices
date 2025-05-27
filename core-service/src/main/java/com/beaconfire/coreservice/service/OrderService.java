package com.beaconfire.coreservice.service;

import com.beaconfire.coreservice.dao.OrderDao;
import com.beaconfire.coreservice.domain.Order;
import com.beaconfire.coreservice.domain.OrderProduct;
import com.beaconfire.coreservice.domain.complementary.Product;
import com.beaconfire.coreservice.domain.complementary.Stats;
import com.beaconfire.coreservice.dto.OrderDetailDto;
import com.beaconfire.coreservice.exception.BadRequestException;
import com.beaconfire.coreservice.exception.NotAuthorizedException;
import com.beaconfire.coreservice.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {
    private final OrderDao orderDao;
    private final AggregatorService aggregatorService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    public OrderService(OrderDao orderDao, AggregatorService aggregatorService, InventoryService inventoryService, PaymentService paymentService) {
        this.orderDao = orderDao;
        this.aggregatorService = aggregatorService;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    @Transactional
    public Long createOrder(Long userId, List<Product> products){
        Order order = Order.builder()
                .userId(userId)
                .build();

        // get the latest info from DB
        List<Product> latestProducts = aggregatorService
                .getProductsFromCatalogService(products.stream().map(Product::getProductId).toList());

        // update the retail price and wholesale price
        Map<Long,BigDecimal> latestPriceRetail = new HashMap<>();
        Map<Long,BigDecimal> latestPriceWholesale = new HashMap<>();
        latestProducts.forEach(product -> {
            latestPriceRetail.put(product.getProductId(), product.getPriceRetail());
            latestPriceWholesale.put(product.getProductId(), product.getPriceWholesale());
        });

        products.forEach(product -> {
            OrderProduct orderProduct = OrderProduct.builder()
                .productId(product.getProductId())
                .priceRetailAtPurchase(latestPriceRetail.get(product.getProductId()))
                .priceWholesaleAtPurchase(latestPriceWholesale.get(product.getProductId()))
                .quantity(product.getQuantity())
                .order(order)
                .build();
            order.getOrderProducts().add(orderProduct);
        });

        return orderDao.addOrder(order);
    }

    /** Get order by id.
     * Notice: the priceRetail and priceWholesale reflects the amount at purchase
     * @param orderId the id of the order
     * @param viewerId the id of the user who is viewing the order
     * @param viewerRole the role of the user who is viewing the order
     * @return the order detail dto
     * */
    @Transactional
    public OrderDetailDto getOrderById(Long orderId, Long viewerId, String viewerRole){
        Order order = orderDao.findById(orderId)
                .orElseThrow(()->new NotFoundException("Order "+orderId+" Not Found"));
        if(!Objects.equals(order.getUserId(), viewerId) && !viewerRole.equals("ADMIN")){
            throw new NotAuthorizedException("You are not authorized to view this order");
        }
        List<OrderProduct> orderProducts = order.getOrderProducts();
        double totalPrice = order.getOrderProducts().stream()
                .reduce(BigDecimal.ZERO,
                        (subtotal, op) -> subtotal.add(op.getPriceRetailAtPurchase().multiply(new BigDecimal(op.getQuantity()))),
                        BigDecimal::add)
                .doubleValue();
        int totalQty = order.getOrderProducts().stream()
                .reduce(0,
                        (subtotal, op) -> subtotal + op.getQuantity(),
                        Integer::sum);

        Map<Long,BigDecimal> latestPriceRetail = new HashMap<>();
        Map<Long,BigDecimal> latestPriceWholesale = new HashMap<>();

        orderProducts.forEach(orderProduct -> {
            latestPriceRetail.put(orderProduct.getProductId(), orderProduct.getPriceRetailAtPurchase());
            latestPriceWholesale.put(orderProduct.getProductId(), orderProduct.getPriceWholesaleAtPurchase());
        });
        List<Product> products = aggregatorService.getProductFromCatalogServiceWithQuantity(orderProducts);
        products.forEach(product -> {
            product.setPriceRetail(latestPriceRetail.get(product.getProductId()));
            product.setPriceWholesale(latestPriceWholesale.get(product.getProductId()));
        });
        return OrderDetailDto.builder()
                .orderId(orderId)
                .userId(order.getUserId())
                .status(order.getStatus())
                .products(products)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .totalPrice(totalPrice)
                .totalQuantity(totalQty)
                .build();
    }

    @Transactional
    public void updateOrderStatus(Long orderId, boolean paymentSuccess, Long updatedBy){
        orderDao.updateStatusByOrderId(orderId, paymentSuccess? "COMPLETED":"CANCELLED", updatedBy);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId, String userRole){
        Order order = orderDao.findById(orderId).orElseThrow(()->new NotFoundException("Order "+orderId+" Not Found"));
        if(!userRole.equals("ADMIN") && !Objects.equals(userId, order.getUserId())){
            throw new NotAuthorizedException("You are not authorized to cancel this order");
        }
        if(!order.getStatus().equals("PROCESSING")){
            throw new BadRequestException("Order is not in PROCESSING status");
        }
        // Inventory rollback
        List<OrderProduct> orderProducts = order.getOrderProducts();
        for(OrderProduct orderProduct:orderProducts){
            inventoryService.addInventory(orderProduct.getProductId(), orderProduct.getQuantity());
        }
        // Cancel payment, cancel order
        paymentService.updatePaymentStatus(orderId,false);
        updateOrderStatus(orderId, false, userId);

    }

    @Transactional
    public void completeOrder(Long orderId, Long userId, String userRole){
        Order order = orderDao.findById(orderId).orElseThrow(()-> new NotFoundException("Order "+orderId+" Not Found"));
        if(!userRole.equals("ADMIN")){
            throw new NotAuthorizedException("You are not authorized to complete this order");
        }
        if(!order.getStatus().equals("PROCESSING")){
            throw new BadRequestException("Order is not in PROCESSING status");
        }

        // Complete payment, complete order
        paymentService.updatePaymentStatus(orderId, true);
        updateOrderStatus(orderId, true, userId);
    }

    /**
     * Get all orders by userId
     * calculating the totalAmount
     * @param userId
     * @return List<Order>
     */
    public List<Order> getOrdersByUserId(Long userId){
        List<Order> orders = orderDao.getOrdersByUserId(userId);
        orders.forEach(order ->{
            BigDecimal total = order.getOrderProducts().stream()
                    .reduce(BigDecimal.ZERO,
                            (subtotal, op) -> subtotal.add(op.getPriceRetailAtPurchase().multiply(new BigDecimal(op.getQuantity()))),
                            BigDecimal::add);
            int totalQty = order.getOrderProducts().stream()
                    .reduce(0,
                            (subtotal, op) -> subtotal + op.getQuantity(),
                            Integer::sum);
            order.setTotalPrice(total.doubleValue());
            order.setTotalQuantity(totalQty);
        });
        return orders;
    }

    public List<Order> getAllOrders(Long userId, String userRole){
        if (!userRole.equals("ADMIN")){
            throw new NotAuthorizedException("You are not authorized to get all orders");
        }
        List<Order> orders = orderDao.findAll();
        orders.forEach(order ->{
            BigDecimal total = order.getOrderProducts().stream()
                    .reduce(BigDecimal.ZERO,
                            (subtotal, op) -> subtotal.add(op.getPriceRetailAtPurchase().multiply(new BigDecimal(op.getQuantity()))),
                            BigDecimal::add);
            int totalQty = order.getOrderProducts().stream()
                    .reduce(0,
                            (subtotal, op) -> subtotal + op.getQuantity(),
                            Integer::sum);
            order.setTotalPrice(total.doubleValue());
            order.setTotalQuantity(totalQty);
        });
        return orders;
    }

    public List<Product> getMostFrequentPurchasedProducts(Integer num, Long userId){
        List<Long> productIds = orderDao.getMostFrequentPurchasedProducts(num, userId);
        return aggregatorService.getProductsFromCatalogService(productIds);
    }

    public List<Product> getMostRecentPurchasedProducts(Integer num, Long userId){
        List<Long> productIds = orderDao.getMostRecentPurchasedProducts(num, userId);
        return aggregatorService.getProductsFromCatalogService(productIds);
    }

    @Transactional(readOnly = true)
    protected Map<Long, Stats> getStats(){
        List<Order> orders = orderDao.getAllCompleted();
        List<OrderProduct> orderProducts = orders.stream().map(Order::getOrderProducts).reduce(
                new ArrayList<>(),
                (acc, orderProduct) -> {
                    acc.addAll(orderProduct);
                    return acc;
                }
        );
        Map<Long, Double> profitMap = new HashMap<>();
        Map<Long, Integer> salesMap = new HashMap<>();
        Map<Long, Stats> statsMap = new HashMap<>();
        for(OrderProduct orderProduct: orderProducts){
            Long productId = orderProduct.getProductId();
            double newProfit = profitMap.getOrDefault(productId, 0.0)+
                    (orderProduct.getPriceRetailAtPurchase().subtract(orderProduct.getPriceWholesaleAtPurchase()))
                            .multiply(new BigDecimal(orderProduct.getQuantity())).doubleValue();
            profitMap.put(productId,newProfit);
            salesMap.put(productId, salesMap.getOrDefault(productId, 0) + orderProduct.getQuantity());
            statsMap.put(productId, Stats.builder()
                    .profit(newProfit)
                    .sales(salesMap.get(productId))
                    .build());
        }

        return statsMap;
    }

    @Transactional(readOnly = true)
    public List<Product> getMostProfitableProducts(Integer num, String userRole){
        if(!userRole.equals("ADMIN")){
            throw new NotAuthorizedException("You are not authorized to get this stats");
        }
        Map<Long,Stats> statsMap = getStats();
        List<Product> products = aggregatorService.getProductsFromCatalogService(statsMap.keySet().stream().toList());

        products.forEach(product -> product.setStats(statsMap.get(product.getProductId())));
        products = products.stream().sorted((p1, p2) -> p2.getStats().getProfit().compareTo(p1.getStats().getProfit())).toList();
        return products.subList(0, Math.min(num, products.size()));
    }

    @Transactional(readOnly = true)
    public List<Product> getMostPopularProducts(Integer num, String userRole){
        if(!userRole.equals("ADMIN")){
            throw new NotAuthorizedException("You are not authorized to get this stats");
        }
        Map<Long,Stats> statsMap = getStats();
        List<Product> products = aggregatorService.getProductsFromCatalogService(statsMap.keySet().stream().toList());
        products.forEach(product -> product.setStats(statsMap.get(product.getProductId())));
        products = products.stream().sorted((p1, p2) -> p2.getStats().getSales().compareTo(p1.getStats().getSales())).toList();
        return products.subList(0, Math.min(num, products.size()));
    }

}
