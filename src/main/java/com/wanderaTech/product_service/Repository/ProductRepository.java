package com.wanderaTech.product_service.Repository;

import com.wanderaTech.product_service.Model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductId(String productId);

    List<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);
   Optional<List<Product>>findBySellerId(String userId,Pageable pageable) ;
    Optional<List<Product>>findByCategoryName(String categoryName, Pageable pageable);
   Optional <Product> findByProductId(String productId);
}
