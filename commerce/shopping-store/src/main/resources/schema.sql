CREATE TABLE IF NOT EXISTS products (
    product_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_name VARCHAR(255),
    description TEXT,
    image_src VARCHAR(500),
    quantity_state VARCHAR(255),
    product_state VARCHAR(255),
    product_category VARCHAR(255),
    price DOUBLE
);