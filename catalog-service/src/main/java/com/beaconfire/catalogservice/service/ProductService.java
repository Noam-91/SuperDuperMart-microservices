package com.beaconfire.catalogservice.service;

import com.beaconfire.catalogservice.dao.CategoryDao;
import com.beaconfire.catalogservice.domain.Category;
import com.beaconfire.catalogservice.domain.Inventory;
import com.beaconfire.catalogservice.domain.Product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductCacheService productCacheService;
    private final CategoryDao categoryDao;
    public ProductService(ProductCacheService productCacheService, CategoryDao categoryDao) {
        this.productCacheService = productCacheService;
        this.categoryDao = categoryDao;
    }

    public List<Product> getAllProducts(String userRole) {
        if(!userRole.equals("ADMIN")){
            throw new RuntimeException("You are not authorized to view all products");
        }
        return productCacheService.findAll();
    }

    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    public List<Product> getAllActiveProducts(String userRole) throws RuntimeException{
        if(!userRole.equals("ADMIN")){
            throw new RuntimeException("You are not authorized to view all products");
        }
        return productCacheService.findAllActive();
    }

    public Product getProductById(Long productId) {
        return productCacheService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    /** Designed for webclient **/
    public List<Product> getBatchProductsById(List<Long> productIds) {
        return productCacheService.findAllByIds(productIds);
    }

    public List<Product> getAllProductsInStock() {
        List<Product> products = productCacheService.findAllActive();
        products.removeIf(product -> product.getInventory().getQuantity()<= 0);
        if(products.isEmpty()){
            throw new RuntimeException("No product in stock");
        }
        return products;
    }

    /** Update product by id
     * if name is changed, create a new product and deactivate the old one
     * @param productId
     * @param product
     * @return
     */
    @Transactional
    public Product updateProduct(Long productId, Product product, Long updatedBy, String userRole) throws RuntimeException {
        if (!userRole.equals("ADMIN")) {
            throw new RuntimeException("You are not authorized to update this product");
        }

        Product productToUpdate = productCacheService.findById(
                productId).orElseThrow(() -> new RuntimeException("Product not found")
        );
        productCacheService.invalidateProductAllCache();
        // name change will create a new product, and deactivate the old one.
        if(!productToUpdate.getName().equals(product.getName())){
            Product newProductSaved = createProduct(product, updatedBy, userRole);

            productToUpdate.setIsActive(false);
            productToUpdate.setUpdatedBy(updatedBy);
            productCacheService.save(productToUpdate);
            return newProductSaved;
        }

        // otherwise, update the product
        productToUpdate.setDescription(product.getDescription());
        productToUpdate.setPriceRetail(product.getPriceRetail());
        productToUpdate.setPriceWholesale(product.getPriceWholesale());
        productToUpdate.setImageUrl(product.getImageUrl());
        productToUpdate.setIsActive(product.getIsActive());
        productToUpdate.setCategory(product.getCategory());
        Inventory inventory = productToUpdate.getInventory();
        inventory.setQuantity(product.getInventory().getQuantity());
        productToUpdate.setUpdatedBy(updatedBy);
        return productCacheService.save(productToUpdate);
    }

    @Transactional
    public Product createProduct(Product product, Long createdBy, String userRole) {
        if (!userRole.equals("ADMIN")) {
            throw new RuntimeException("You are not authorized to create a product");
        }
        Product newProduct = Product.builder()
                .name(product.getName())
                .description(product.getDescription())
                .priceRetail(product.getPriceRetail())
                .priceWholesale(product.getPriceWholesale())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .category(product.getCategory())
                .createdBy(createdBy)
                .build();
        Inventory inventory = Inventory.builder()
                .product(newProduct)
                .quantity(product.getInventory().getQuantity())
                .build();

        newProduct.setInventory(inventory);
        productCacheService.invalidateProductAllCache();
        return productCacheService.save(newProduct);
    }
}
