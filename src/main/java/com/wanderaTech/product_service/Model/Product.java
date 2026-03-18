package com.wanderaTech.product_service.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products_table")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;

    @Column(name = "seller_id")
    private String sellerId;  //reference to  seller

    @Column(name = "product_name")
    @NotBlank(message = "product_name required")
    private String productName;

    @Column(name = "product_description")
    @NotBlank(message = "description required")
    private String productDescription;

    @Column(name = "product_price")
    private Double price;

    @Column(name = "category_name")
    @NotBlank(message = "category_name required")
    private String categoryName;
    private String imageUrl;
}
