package com.wanderaTech.product_service.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderaTech.common_events.productEvent.ProductCreatedEvent;
import com.wanderaTech.product_service.KafkaConfig.StockProducer;
import com.wanderaTech.product_service.Model.OutboxEvent;
import com.wanderaTech.product_service.Model.Product;
import com.wanderaTech.product_service.ProductDto.ProductRequest;
import com.wanderaTech.product_service.ProductDto.ProductResponse;
import com.wanderaTech.product_service.Repository.OutboxRepository;
import com.wanderaTech.product_service.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImplementation implements ProductServiceInterface {

    private final ProductRepository productRepository;
    private final StockProducer stockProducer;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    //this method creates product then  sends  the event to inventory to initialize product  stock (kafka )
//    @CachePut(value = "createProduct", key = "#result.productId")
    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {

        //  Map productRequest to Product
        Product product = toEntity(productRequest);
        var savedProduct = productRepository.save(product);

        // Convert to Response
        ProductResponse response = toProductDto(savedProduct);

        //  Prepare the Event Object for stock initialization (Kafka)
        ProductCreatedEvent event = new ProductCreatedEvent(
                response.getProductId(),
                response.getProductName(),
                savedProduct.getUserId(),
                productRequest.getStock()
        );

        //  Attempt Kafka Send event to the inventory to initialize the product stock
        //use the ddl if the kafka is low the event is saved in the outbox for retries if the kafka return  online
        try {
            log.info("Attempting to send ProductCreatedEvent for ID: {}", response.getProductId());
            stockProducer.sendInitialStock(event);
        } catch (Exception e) {
            log.error("Kafka failure for product  id {}. Saving to Outbox for retry.", response.getProductId());

            // 4. Save to Outbox Table if Kafka fails
            OutboxEvent outboxEntry = OutboxEvent.builder()
                    .aggregateId(response.getProductId())
                    .eventType("PRODUCT_STOCK_INIT")
                    .payload(serializeToJson(event))
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();

            outboxRepository.save(outboxEntry);
        }

        return response;
    }

    // Helper method using injected ObjectMapper
    private String serializeToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to serialize event", ex);
        }
    }

//    @Cacheable(
//            value = "productsByCategory",
//            key = "#categoryName + '_' + #page + '_' + #size"
//    )
    @Override
    public List<ProductResponse> getProductsByCategory(String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Pass the pageable object to your repository
        List<Product> products = productRepository.findByCategoryName(categoryName, pageable)
                .orElseThrow(() -> new RuntimeException("No product in this category: " + categoryName));

        return products.stream()
                .map(product -> new ProductResponse(
                        String.valueOf(product.getProductId()),
                        product.getProductName(),
                        product.getProductDescription(),
                        product.getPrice(),
                        product.getUserId()
                ))
                .toList();
    }

//    @Cacheable(
//            value = "productsByName",
//            key = "#productName + '_' + #page + '_' + #size",
//            condition = "#productName != null && #productName.length() > 3",
//            unless = "#result.isEmpty()"
//    )
    @Override
    public List<ProductResponse> getProductByName(String productName,int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        // If name is null or empty → return all products
        if (productName == null || productName.isBlank()) {
            return productRepository.findAll(pageable)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }


        //the search name is fuzzy ( give the  result of the nearing name typed if not searched exact name )

        List<Product> products =
                productRepository.findByProductNameContainingIgnoreCase(productName,pageable);

        // If found → return matched products
        if (!products.isEmpty()) {
            return products.stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        // If NOT found → return all products
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

//    @Cacheable(
//            value = "allProducts",
//            key = "#page + '_' + #size",
//            unless = "#result.isEmpty()"
//    )
    @Override
    public List<ProductResponse> findAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


         //find all product that a seller has added in the product-service
//         @Cacheable(
//                 value = "productsBySeller",
//                 key = "#sellerId + '_' + #page + '_' + #size",
//                 unless = "#result.isEmpty()"
//         )
    @Override
    public List<ProductResponse> getProductUnderSellerId(String userId,int page, int size) {
             Pageable pageable = PageRequest.of(page, size);
             List<Product> products = productRepository.findByUserId(userId,pageable)
                .orElseThrow(()->new RuntimeException("No product found under the sellerId: " + userId));


        // Map each Product entity to ProductResponse
        return products.stream()
                .map(product -> new ProductResponse(
                        String.valueOf(product.getProductId()),
                        product.getProductName(),
                        product.getProductDescription(),
                        product.getPrice(),
                        product.getUserId()
                ))
                .toList();
    }

//    @Cacheable(
//            value = "findByProductId",
//            key = "#productId",
//            unless = "#result == null"
//    )
    @Override
    public ProductResponse findByProductId(String productId) {
        Product product=productRepository.findByProductId(productId)
                .orElseThrow(()-> new RuntimeException("Product with id not found "));

        return toProductDto(product);

    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductName(product.getProductName());
        productResponse.setProductDescription(product.getProductDescription());
        productResponse.setPrice(product.getPrice());
        return productResponse;

    }


    private ProductResponse toProductDto(Product saveProduct) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(saveProduct.getProductId());
        productResponse.setProductName(saveProduct.getProductName());
        productResponse.setPrice(saveProduct.getPrice());
        productResponse.setUserId(saveProduct.getUserId());
        productResponse.setProductDescription(saveProduct.getProductDescription());
        return productResponse;
    }


    private Product toEntity(ProductRequest productRequest) {
        return Product.builder()

                .productId(generateProductId())
                .productName(productRequest.getProductName())

                .productDescription(productRequest.getProductDescription())
                .price(productRequest.getPrice())
                .categoryName(productRequest.getCategoryName())
                .imageUrl(productRequest.getImageUrl())
                .build();
    }

    private String generateProductId() {
        String productId;
        do {
            // Generate random 6-digit number
            int randomNum = 100000 + new Random().nextInt(900000); // 100000 to 999999
            productId = String.valueOf(randomNum);
            // Loop until the productId is unique
        } while (productRepository.existsByProductId(productId));

        return productId;
    }

}
