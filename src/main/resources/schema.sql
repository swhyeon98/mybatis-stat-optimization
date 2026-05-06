DROP TABLE IF EXISTS customer_inquiry;

CREATE TABLE customer_inquiry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(30) NOT NULL,
    channel VARCHAR(30) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    title VARCHAR(255) NOT NULL,
    customer_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    INDEX idx_inquiry_category_status (category, status),
    INDEX idx_inquiry_created_at (created_at),
    INDEX idx_inquiry_channel (channel),
    INDEX idx_inquiry_priority (priority)
);
