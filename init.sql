CREATE DATABASE IF NOT EXISTS superdupermart;
USE superdupermart;

DROP TABLE IF EXISTS User;
CREATE TABLE User(
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE KEY,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT
);

DROP TABLE IF EXISTS UserProfile;
CREATE TABLE UserProfile(
    userprofile_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    zip VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT,

    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS Category;
CREATE TABLE Category(
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE KEY,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

DROP TABLE IF EXISTS Product;
CREATE TABLE Product(
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price_retail DECIMAL(10,2) NOT NULL,
    price_wholesale DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,

    FOREIGN KEY (category_id) REFERENCES Category(category_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS Inventory;
CREATE TABLE Inventory(
    inventory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT chk_inventory_quantity_non_negative CHECK (quantity >= 0)

);

DROP TABLE IF EXISTS CartProduct;
CREATE TABLE CartProduct(
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS OrderTable;
CREATE TABLE OrderTable(
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'PROCESSING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT,

    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS OrderProduct;
CREATE TABLE OrderProduct(
    orderproduct_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    price_retail_at_purchase DECIMAL(10, 2) NOT NULL,
    price_wholesale_at_purchase DECIMAL(10, 2) NOT NULL,
    quantity BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES OrderTable(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS Watchlist;
CREATE TABLE Watchlist(
    watchlist_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS WatchlistProduct;
CREATE TABLE WatchlistProduct(
    watchlistproduct_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    watchlist_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    FOREIGN KEY (watchlist_id) REFERENCES Watchlist(watchlist_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS Payment;
CREATE TABLE Payment(
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    payment_intent_id VARCHAR(255),
    type VARCHAR(255),
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES OrderTable(order_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# ----------------------------INITIALIZATION--------------------------------------
# --------------User-----------------
INSERT INTO User (user_id, username, password, is_admin) VALUES (1, 'admin', '$2a$10$VBfM4UCxutlPUytMjM2BU.lrZ8urDVo/axmWfcKPs680gbd0fpYzm', TRUE);
INSERT INTO UserProfile (userprofile_id, user_id, first_name, last_name, email, address, city, state, country, zip)
VALUES (1, 1, 'Admin', 'Admin', 'admin@superdupermart.com', '999 Admin Drive', 'NYC', 'NY', 'U.S.', '12345');

# --------------Category-----------------
INSERT INTO Category (category_id, name) VALUES (1, 'Book'), (2,'Sport');

# --------------Product-----------------
INSERT INTO Product (product_id, category_id, name, description, price_retail, price_wholesale, image_url) VALUES
(1,1,'System Design Interview – An Insider''s Guide','System Design',99.99,59.98,'https://m.media-amazon.com/images/I/51lJolln98L.jpg'),
(2,1,'Behavioral Interviews for Software Engineers','All the Must-Know Questions With Proven Strategies and Answers That Will Get You the Job',56.78,34.82,'https://m.media-amazon.com/images/I/61+0s3HR--L._AC_UF1000,1000_QL80_.jpg'),
(3,2,'Selkirk SLK Evo 2.0 Max Pickleball Paddle','Evo Power | Evo Control | Evo Hybrid | Fiberglass Pickleball Racket | Carbon Fiber Pickleball Paddle with SpinFlex Surface',50.99,35.00,'https://m.media-amazon.com/images/I/61VkRcTYMXL.__AC_SX300_SY300_QL70_FMwebp_.jpg'),
(4,2,'Spalding React TF-250 Indoor-Outdoor Basketball','Official size and weight: Size 7, 29.5" | All-surface composite leather cover | Designed for indoor and outdoor play',45.00,19.00,'https://m.media-amazon.com/images/I/91v2PaVh7qL._AC_SX679_.jpg');

# --------------Inventory-----------------
INSERT INTO Inventory (inventory_id, product_id, quantity) VALUES
(1, 1, 10), (2, 2, 2), (3, 3, 7), (4, 4, 0);