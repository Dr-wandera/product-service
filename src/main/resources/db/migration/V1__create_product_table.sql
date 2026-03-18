
CREATE TABLE IF NOT EXISTS product_table (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id VARCHAR(255) NOT NULL,
    seller_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL DEFAULT 0,
    category_id VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY unique_product_id (product_id)
)
