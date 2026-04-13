CREATE TABLE IF NOT EXISTS wares (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN NOT NULL,
    dimension_id UUID NOT NULL,
    width DECIMAL(10, 8) NOT NULL,
    height DECIMAL(10, 8) NOT NULL,
    depth DECIMAL(10, 8) NOT NULL,
    weight DECIMAL(10, 8) NOT NULL,
    quantity INTEGER NOT NULL
)