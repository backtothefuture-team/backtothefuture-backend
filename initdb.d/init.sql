DROP
DATABASE IF EXISTS test;
CREATE
DATABASE IF NOT EXISTS test;

USE test;

CREATE TABLE member
(
    member_id    bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    auth_id      varchar(255),
    email        varchar(255) UNIQUE,
    name         varchar(255),
    password     varchar(255),
    phone_number varchar(255) UNIQUE,
    status       varchar(255),
    profile       varchar(255),
    provider     varchar(255),
    roles        varchar(255),
    updated_at   datetime(6),
    updated_by   varchar(255),
    created_at   datetime(6),
    created_by   varchar(255)
);


CREATE TABLE store
(
    store_id    bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        varchar(255),
    description TEXT,
    location    varchar(255),
    contact     varchar(255),
    image       varchar(255),
    member_id   bigint NOT NULL,
    updated_at  datetime(6),
    updated_by  varchar(255),
    created_at  datetime(6),
    created_by  varchar(255),
    start_time  time,
    end_time    time,
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE product
(
    product_id     bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name           varchar(255),
    description    TEXT,
    price          int,
    stock_quantity int,
    thumbnail      varchar(255),
    store_id       bigint NOT NULL,
    updated_at     datetime(6),
    updated_by     varchar(255),
    created_at     datetime(6),
    created_by     varchar(255),
    FOREIGN KEY (store_id) REFERENCES store (store_id)
);

CREATE TABLE IF NOT EXISTS review
(
    review_id       BIGINT     NOT NULL    AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT     NOT NULL,
    store_id        BIGINT     NOT NULL,
    rating_count    DOUBLE     NOT NULL,
    content         TEXT,
    image_url       VARCHAR(255),
    updated_at      datetime(6),
    updated_by      varchar(255),
    created_at      datetime(6),
    created_by      varchar(255)
);

CREATE TABLE reservation
(
    reservation_id   bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id        bigint NOT NULL,
    store_id         bigint NOT NULL,
    total_price      int,
    updated_at       datetime(6),
    updated_by       varchar(255),
    created_at       datetime(6),
    created_by       varchar(255),
    reservation_time time,
    FOREIGN KEY (store_id) REFERENCES store (store_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE reservation_product
(
    reservation_product_id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quantity               int,
    reservation_id         bigint NOT NULL,
    product_id             bigint NOT NULL,
    updated_at             datetime(6),
    updated_by             varchar(255),
    created_at             datetime(6),
    created_by             varchar(255),
    FOREIGN KEY (reservation_id) REFERENCES reservation (reservation_id),
    FOREIGN KEY (product_id) REFERENCES product (product_id)
);

INSERT INTO member (member_id, auth_id, email, name, password, phone_number, status, provider, roles, updated_at,
                    updated_by, created_at, created_by)
VALUES (1, null, 'email@naver.com', '이상민', 'mmsc532mmmm', '010-0000-0000', 'ACTIVE', null, 'ROLE_STORE_OWNER', null,
        null,
        null, null);

INSERT INTO store (store_id, name, description, location, contact, image, member_id, updated_at, updated_by, created_at,
                   created_by, start_time, end_time)
VALUES (1, 'test', 'test', null, null, null, 1, null, null, null, null, '10:00', '21:00');

INSERT INTO product (product_id, name, description, price, stock_quantity, thumbnail, store_id, updated_at, updated_by,
                     created_at, created_by)
VALUES (1, 'test', 'test', 1000, 10, null, 1, null, null, null, null),
       (2, 'test', 'test', 2000, 10, null, 1, null, null, null, null);
