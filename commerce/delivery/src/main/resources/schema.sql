CREATE TABLE IF NOT EXISTS addresses (
    id UUID PRIMARY KEY,
    country VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    house VARCHAR(255),
    flat VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY,
    from_address UUID,
    to_address UUID,
    delivery_state VARCHAR(255),

    CONSTRAINT fk_on_from_address FOREIGN KEY (from_address) REFERENCES addresses (id),
    CONSTRAINT fk_on_to_address FOREIGN KEY (to_address) REFERENCES addresses (id)
);