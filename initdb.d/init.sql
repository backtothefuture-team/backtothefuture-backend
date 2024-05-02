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
    store_id     bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name         varchar(255),
    description  TEXT,
    location     varchar(255),
    contact      varchar(255),
    image        varchar(255),
    member_id    bigint NOT NULL,
    updated_at   datetime(6),
    updated_by   varchar(255),
    created_at   datetime(6),
    created_by   varchar(255),
    rating       double,
    rating_count int,
    start_time   time,
    end_time     time,
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

INSERT INTO store (name, description, location, contact, image, member_id, updated_at, updated_by, created_at, created_by, rating, rating_count, start_time, end_time)
VALUES
('상점1', '상점1 - 설명', 'Location 1', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:00:00', '18:00:00'),
('상점2', '상점2 - 설명', 'Location 2', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '10:00:00', '19:00:00'),
('상점3', '상점3 - 설명', 'Location 3', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '08:00:00', '17:00:00'),
('상점4', '상점4 - 설명', 'Location 4', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:30:00', '18:30:00'),
('상점5', '상점5 - 설명', 'Location 5', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '10:30:00', '19:30:00'),
('상점6', '상점6 - 설명', 'Location 6', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '08:30:00', '17:30:00'),
('상점7', '상점7 - 설명', 'Location 7', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:00:00', '18:00:00'),
('상점8', '상점8 - 설명', 'Location 8', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '10:00:00', '19:00:00'),
('상점9', '상점9 - 설명', 'Location 9', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '08:00:00', '17:00:00'),
('상점10', '상점10 - 설명', 'Location 10', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:30:00', '18:30:00'),
('상점11', '상점11 - 설명', 'Location 11', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '10:30:00', '19:30:00'),
('상점12', '상점12 - 설명', 'Location 12', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '08:30:00', '17:30:00'),
('상점13', '상점13 - 설명', 'Location 13', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:00:00', '18:00:00'),
('상점14', '상점14 - 설명', 'Location 14', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '10:00:00', '19:00:00'),
('상점15', '상점15 - 설명', 'Location 15', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '08:00:00', '17:00:00'),
('상점16', '상점16 - 설명', 'Location 16', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:30:00', '18:30:00'),
('상점17', '상점17 - 설명', 'Location 17', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '10:30:00', '19:30:00'),
('상점18', '상점18 - 설명', 'Location 18', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '08:30:00', '17:30:00'),
('상점19', '상점19 - 설명', 'Location 19', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:00:00', '18:00:00'),
('상점20', '상점20 - 설명', 'Location 20', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '10:00:00', '19:00:00');

INSERT INTO product (product_id, name, description, price, stock_quantity, thumbnail, store_id, updated_at, updated_by,
                     created_at, created_by)
VALUES (1, 'test', 'test', 1000, 10, null, 1, null, null, null, null),
       (2, 'test', 'test', 2000, 10, null, 1, null, null, null, null);
