package com.wanderaTech.product_service.Controller;

import com.wanderaTech.product_service.ProductDto.ProductRequest;
import com.wanderaTech.product_service.ProductDto.ProductResponse;
import com.wanderaTech.product_service.Service.ProductServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductServiceImplementation productServiceImplementation;

    @PostMapping("/add")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody ProductRequest request) {

        ProductResponse response = productServiceImplementation.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/category/{categoryName}")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProductsUnderCategory(
                       @PathVariable String categoryName ,
                        @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size
             ) {
        return productServiceImplementation.getProductsByCategory(categoryName,page,size);
    }

    @GetMapping("/search/name/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProductByName(
            @PathVariable String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productServiceImplementation.getProductByName(productName, page, size);
    }


    @GetMapping("/{productId}")
    public ProductResponse findByProductId(@PathVariable String productId) {
        return productServiceImplementation.findByProductId(productId);
    }

    @GetMapping("/get/allProduct")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> findAllProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return productServiceImplementation.findAllProducts(page,size);
    }

    @GetMapping("/seller/{sellerId}")
    public List<ProductResponse> findProductUnderSellerId(
            @PathVariable String sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productServiceImplementation.getProductUnderSellerId(sellerId,page,size);
    }

}
