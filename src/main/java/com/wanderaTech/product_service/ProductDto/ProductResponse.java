package com.wanderaTech.product_service.ProductDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse  {
    private String productId;
    private String productName;
    private String productDescription;
    private Double price;
    private String userId;

}
