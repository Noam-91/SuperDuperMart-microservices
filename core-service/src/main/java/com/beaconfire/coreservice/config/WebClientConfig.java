package com.beaconfire.coreservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced // This annotation is essential for service ID resolution and load balancing
    public WebClient.Builder loadBalancedWebClientBuilder() {
        // Spring Boot/Cloud automatically provides a builder that is integrated
        // with the DiscoveryClient. We just need to define a bean of type
        // WebClient.Builder and annotate it with @LoadBalanced.
        return WebClient.builder();
    }

    /**
     * Provides a WebClient instance built from the loadBalancedWebClientBuilder.
     * This is the WebClient instance you will inject into your services (like AggregatorService).
     *
     * @param loadBalancedWebClientBuilder The LoadBalanced WebClient.Builder bean.
     * @return A WebClient instance capable of calling services by ID.
     */
    @Bean
    public WebClient webClient(WebClient.Builder loadBalancedWebClientBuilder) {
        // Build the WebClient using the LoadBalanced builder
        return loadBalancedWebClientBuilder.build();
    }

}
