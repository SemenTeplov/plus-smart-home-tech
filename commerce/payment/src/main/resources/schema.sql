CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    total_price DECIMAL(10, 2),
    delivery_price DECIMAL(10, 2),
    fee_total DECIMAL(10, 2),
    status VARCHAR(255)
);