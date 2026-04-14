CREATE TABLE IF NOT EXISTS carts (
    id UUID PRIMARY KEY,
    username VARCHAR(255),
    state VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    cart_id UUID,
    count_products INTEGER
);