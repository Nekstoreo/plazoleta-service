CREATE TABLE restaurants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    nit VARCHAR2(255) NOT NULL UNIQUE,
    address VARCHAR2(255) NOT NULL,
    phone VARCHAR2(13) NOT NULL,
    logo_url VARCHAR2(255) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dishes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    price INTEGER NOT NULL,
    description VARCHAR2(500) NOT NULL,
    image_url VARCHAR2(255) NOT NULL,
    category VARCHAR2(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    restaurant_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dish_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    employee_id BIGINT,
    status VARCHAR2(20) NOT NULL,
    security_pin VARCHAR2(6),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_item_dish FOREIGN KEY (dish_id) REFERENCES dishes(id)
);

CREATE INDEX idx_dishes_restaurant_id ON dishes(restaurant_id);
CREATE INDEX idx_orders_client_id ON orders(client_id);
CREATE INDEX idx_orders_restaurant_id ON orders(restaurant_id);
CREATE INDEX idx_orders_employee_id ON orders(employee_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
