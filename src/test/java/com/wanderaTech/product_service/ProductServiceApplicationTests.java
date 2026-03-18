package com.wanderaTech.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderaTech.product_service.ProductDto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Container
	static MySQLContainer mySQLContainer= new MySQLContainer(DockerImageName.parse("mysql:5.7.34"));

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mySQLContainer::getUsername);
		registry.add("spring.datasource.password", mySQLContainer::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop"); // ensure schema
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product/add")
						.contentType(MediaType.APPLICATION_JSON)  // correct MediaType
						.content(productRequestString))
				.andExpect(status().isCreated());
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.sellerId("1511741")
				.productName("wander")
				.productDescription("is for drinking")
				.categoryName("Electronic")
				.price(123.0)
				.stock(100)
				.imageUrl("silver")
				.build();
	}
    @Test
    void shouldGetAllProducts() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/get/allProduct")
                        .param("page","0")
                        .param("size","10"))
                .andDo(print())   // 👈 this prints the response
                .andExpect(status().isOk());
    }
}