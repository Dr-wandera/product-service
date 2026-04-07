package com.wanderaTech.product_service.ProductDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedRequest {
    private String sellerId;
    private String productName;
    private String productDescription;
    private Double price;
    private String categoryName;
    private Integer stock;
    private String imageUrl;
}
