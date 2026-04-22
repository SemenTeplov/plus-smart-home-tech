CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    username VARCHAR(500),
    shopping_cart_id UUID,
    payment_id UUID,
    delivery_id UUID,
    state VARCHAR(255),
    delivery_weight DECIMAL(10, 2),
    delivery_volume DECIMAL(10, 2),
    fragile BOOLEAN,
    total_price DECIMAL(10, 2),
    delivery_price DECIMAL(10, 2),
    product_price DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    product_id UUID,
    count_products INTEGER,
    CONSTRAINT fk_on_order FOREIGN KEY (product_id) REFERENCES orders (id)
);