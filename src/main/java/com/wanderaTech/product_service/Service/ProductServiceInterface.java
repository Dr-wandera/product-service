package com.wanderaTech.product_service.Service;

import com.wanderaTech.product_service.ProductDto.ProductRequest;
import com.wanderaTech.product_service.ProductDto.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductServiceInterface {
    ProductResponse createProduct(ProductRequest productRequest);

    List<ProductResponse> getProductsByCategory(String categoryName,int page, int  size);

    List<ProductResponse> getProductByName(String productName,int page, int size);

    List<ProductResponse> findAllProducts(int page, int size);

    List<ProductResponse> getProductUnderSellerId(String sellerId,int page,int size);

    ProductResponse findByProductId(String productId);
}
