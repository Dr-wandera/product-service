package com.wanderaTech.product_service.Client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CategoryClient {

    private final WebClient.Builder webClient;

    //this checks if category exist before saving  product to it
    public boolean categoryExists(String categoryName) {

        Boolean response = webClient
                .baseUrl("http://category-service")   // base URL
                .build()
                .get()
                .uri("/api/v1/category/exists/{categoryName}", categoryName) // endpoint
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        return Boolean.TRUE.equals(response);
    }

}
