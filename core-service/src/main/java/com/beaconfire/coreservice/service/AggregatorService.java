package com.beaconfire.coreservice.service;

import com.beaconfire.coreservice.domain.complementary.ProductContainer;
import com.beaconfire.coreservice.domain.complementary.Product;
import com.beaconfire.coreservice.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AggregatorService {
    private final WebClient webClient;
    public AggregatorService(WebClient webClient) {
        this.webClient = webClient;
    }

    /** Get product info from catalog service and return a list of Product
     * @param pcCollection a list of ProductContainer (Order/CartProduct/Watchlist)
     * @return a list of Product
     */
    public List<Product> getProductFromCatalogServiceWithQuantity(List<? extends ProductContainer> pcCollection) {
        List<Long> productIds = pcCollection.stream()
                .map(ProductContainer::getProductId)
                .toList();
        // Webclient get product info
        Product[] products;
        try{
            products = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("catalog-service")
                            .path("/internal/products/batch")
                            .queryParam("productIds", productIds.toArray())
                            .build())
                    .retrieve()
                    .bodyToMono(Product[].class)
                    .block();
            Map<Long, Integer> quantityMap = pcCollection.stream()
                    .collect(Collectors.toMap(ProductContainer::getProductId, ProductContainer::getQuantity));

            List<Product> productList = new ArrayList<>();
            for(Product product:products){
                product.setQuantity(quantityMap.get(product.getProductId()));
                productList.add(product);
            }
            return productList;
        }catch (Exception e){
            return null;
        }
    }

    /** Product without quantity
     * @param productIds a list of productIds
     * @return a list of Product
     */
    public List<Product> getProductsFromCatalogService(List<Long> productIds) {
        Product[] products = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("catalog-service")
                        .path("/internal/products/batch")
                        .queryParam("productIds", productIds.toArray())
                    .build())
                .retrieve()
                .bodyToMono(Product[].class)
                .block();
        if(products == null){
            throw new NotFoundException("WebClient fetching failed");
        }
        return Arrays.stream(products).toList();
    }

}
