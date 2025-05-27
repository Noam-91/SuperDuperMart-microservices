package com.beaconfire.catalogservice.service;

import com.beaconfire.catalogservice.dao.ProductDao;
import com.beaconfire.catalogservice.domain.Product;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductCacheService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProductCacheService.class);
    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductDao productDao;

    public ProductCacheService(RedisTemplate<String, Object> redisTemplate, ProductDao productDao) {
        this.redisTemplate = redisTemplate;
        this.productDao = productDao;
    }

//    @Value("${cache.productList.ttl}")          // 5 min
    private long productListTtl = 300L;
//    @Value("${cache.product.ttl}")              // 60 min
    private long productTtl = 3600L;

    @Transactional
    public Optional<Product> findById(Long productId){
        String cacheKey = "product:" + productId;
        Product cachedProduct = (Product) redisTemplate.opsForValue().get(cacheKey);

        if (cachedProduct != null) {
            return Optional.of(cachedProduct);
        }

        LOGGER.debug("Cache miss for product ID: {}", productId);

        Optional<Product> productFromDb = productDao.findById(productId);
        productFromDb.ifPresent(product ->
                redisTemplate.opsForValue().set(cacheKey, product, Duration.ofSeconds(productTtl))
        );
        LOGGER.debug("Got from DB, Cache refreshed for product ID: {}.", productId);
        return productFromDb;
    }

    public List<Product> findAll(){
        String cacheKey = "productList:all";
        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        if(cachedProducts != null){
            return cachedProducts;
        }
        LOGGER.debug("Cache miss for all products");
        List<Product> activeProductsFromDB = productDao.findAll();

        redisTemplate.opsForValue().set(cacheKey, activeProductsFromDB, Duration.ofSeconds(productListTtl));
        LOGGER.debug("Got from DB, Cache refreshed for all products.");
        return activeProductsFromDB;
    }

    public List<Product> findAllActive(){
        String cacheKey = "productList:active";
        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        if(cachedProducts != null){
            return cachedProducts;
        }

        LOGGER.debug("Cache miss for active products");
        List<Product> activeProductsFromDB = productDao.findAllActive();

        redisTemplate.opsForValue().set(cacheKey, activeProductsFromDB, Duration.ofSeconds(productListTtl));
        LOGGER.debug("Got from DB, Cache refreshed for active products.");
        return activeProductsFromDB;
    }

    @Transactional
    public List<Product> findAllByIds(List<Long> productIds){
        String cacheKey = "productList:byIds:" + productIds.stream().sorted(Long::compare)
                                            .map(String::valueOf)
                                            .collect(Collectors.joining("_"));
        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        if(cachedProducts != null){
            return cachedProducts;
        }
        LOGGER.debug("Cache miss for product collection by Ids. ");

        //Gathering one by one from cache / DB
        cachedProducts = new ArrayList<>();
        for(Long productId: productIds){
            Optional<Product> product = findById(productId);
            product.ifPresent(cachedProducts::add);
        }
        redisTemplate.opsForValue().set(cacheKey, cachedProducts, Duration.ofSeconds(productListTtl));
        return cachedProducts;
    }

    public void invalidateProductCache(Long productId) {
        String cacheKey = "product:" + productId;
        LOGGER.debug("Invalidating individual product cache for ID: {}", productId);
        redisTemplate.delete(cacheKey);
    }

    public void invalidateProductAllCache() {
        String cacheKey = "productList:all";
        LOGGER.debug("Invalidating product list cache for all");
        redisTemplate.delete(cacheKey);
    }

    @Transactional
    public Product save(Product product){
        String cacheKey = "product:" + product.getProductId();
        invalidateProductCache(product.getProductId());
        return productDao.save(product);
    }
}
