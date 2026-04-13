CREATE TABLE IF NOT EXISTS wares (
    product_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    fragile BOOLEAN NOT NULL,
    dimension_id UUID NOT NULL,
    width DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    depth DOUBLE NOT NULL,
    weight DOUBLE NOT NULL,
    quantity INTEGER NOT NULL
)