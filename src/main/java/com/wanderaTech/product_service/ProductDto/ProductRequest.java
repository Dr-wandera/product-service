package com.wanderaTech.product_service.ProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String sellerId;
    private String productName;
    private String productDescription;
    private Double price;
    private String categoryName;
    private Integer stock;
    private String imageUrl;
}
