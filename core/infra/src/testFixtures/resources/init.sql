DROP
    DATABASE IF EXISTS test;
CREATE
    DATABASE IF NOT EXISTS test;

USE test;

CREATE TABLE member
(
    member_id          bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    auth_id            varchar(255),
    email              varchar(255) UNIQUE,
    name               varchar(255),
    password           varchar(255),
    phone_number       varchar(255) UNIQUE,
    status             varchar(255),
    profile            varchar(255),
    birth              date,
    gender             char(1),
    registration_token varchar(255),
    provider           varchar(255),
    roles              varchar(255),
    updated_at         datetime(6),
    updated_by         varchar(255),
    created_at         datetime(6),
    created_by         varchar(255)
);


CREATE TABLE store
(
    store_id           bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sorting_index      bigint,
    name               varchar(255),
    description        TEXT,
    location           varchar(255),
    contact            varchar(255),
    image              varchar(255),
    member_id          bigint NOT NULL,
    updated_at         datetime(6),
    updated_by         varchar(255),
    created_at         datetime(6),
    created_by         varchar(255),
    average_rating     double,
    total_rating_count int,
    start_time         time,
    end_time           time,
    FOREIGN KEY (member_id) REFERENCES member (member_id),
    INDEX idx_sorting_index (sorting_index)
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

CREATE TABLE heart
(
    heart_id   bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id  bigint NOT NULL,
    store_id   bigint NOT NULL,
    updated_at datetime(6),
    updated_by varchar(255),
    created_at datetime(6),
    created_by varchar(255),
    FOREIGN KEY (store_id) REFERENCES store (store_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE term
(
    term_id     bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title       varchar(255),
    content     TEXT,
    is_required boolean,
    updated_at  datetime(6),
    updated_by  varchar(255),
    created_at  datetime(6),
    created_by  varchar(255)
);

CREATE TABLE term_history
(
    term_history_id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    term_id         bigint NOT NULL,
    member_id       bigint NOT NULL,
    is_accepted     boolean,
    updated_at      datetime(6),
    updated_by      varchar(255),
    created_at      datetime(6),
    created_by      varchar(255),
    FOREIGN KEY (term_id) REFERENCES term (term_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE bank
(
    bank_id    bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code       varchar(255),
    name       varchar(255),
    updated_at datetime(6),
    updated_by varchar(255),
    created_at datetime(6),
    created_by varchar(255)
);

CREATE TABLE account
(
    account_id     bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id      bigint NOT NULL,
    bank_id        bigint NOT NULL,
    account_number varchar(255),
    account_holder varchar(255),
    updated_at     datetime(6),
    updated_by     varchar(255),
    created_at     datetime(6),
    created_by     varchar(255),
    FOREIGN KEY (bank_id) REFERENCES bank (bank_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE residence
(
    residence_id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id    bigint NOT NULL,
    latitude     varchar(255),
    longitude    varchar(255),
    address      varchar(255),
    updated_at   datetime(6),
    updated_by   varchar(255),
    created_at   datetime(6),
    created_by   varchar(255),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE reservation_status_history
(
    id             bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    reservation_id bigint NOT NULL,
    order_type     varchar(255),
    event_time     time,
    FOREIGN KEY (reservation_id) REFERENCES reservation (reservation_id)
);

CREATE TABLE IF NOT EXISTS review
(
    review_id  BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT NOT NULL,
    store_id   BIGINT NOT NULL,
    rating     DOUBLE NOT NULL,
    content    TEXT,
    image_url  VARCHAR(255),
    updated_at datetime(6),
    updated_by varchar(255),
    created_at datetime(6),
    created_by varchar(255)
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

/* 약관 데이터*/
INSERT INTO term (term_id, title, content, is_required, updated_at, updated_by, created_at, created_by)
VALUES (1, '만 14세 이상', '상세내용', true, null, null, null, null),
       (2, '서비스 이용 약관', '상세내용', true, null, null, null, null),
       (3, '개인정보 수집/이용 동의', '상세내용', true, null, null, null, null),
       (4, '위치기반서비스 이용약관', '상세내용', true, null, null, null, null),
       (5, '마케팅 활용 동의', '상세내용', false, null, null, null, null);

/* 은행 리스트 */
INSERT INTO bank (code, name, updated_at, updated_by, created_at, created_by)
VALUES ('39', '경남은행', NOW(), 'admin', NOW(), 'admin'),
       ('34', '광주은행', NOW(), 'admin', NOW(), 'admin'),
       ('12', '단위농협', NOW(), 'admin', NOW(), 'admin'),
       ('32', '부산은행', NOW(), 'admin', NOW(), 'admin'),
       ('45', '새마을금고', NOW(), 'admin', NOW(), 'admin'),
       ('64', '산림조합', NOW(), 'admin', NOW(), 'admin'),
       ('88', '신한은행', NOW(), 'admin', NOW(), 'admin'),
       ('48', '신협', NOW(), 'admin', NOW(), 'admin'),
       ('27', '씨티은행', NOW(), 'admin', NOW(), 'admin'),
       ('20', '우리은행', NOW(), 'admin', NOW(), 'admin'),
       ('71', '우체국예금보험', NOW(), 'admin', NOW(), 'admin'),
       ('50', '저축은행중앙회', NOW(), 'admin', NOW(), 'admin'),
       ('37', '전북은행', NOW(), 'admin', NOW(), 'admin'),
       ('35', '제주은행', NOW(), 'admin', NOW(), 'admin'),
       ('90', '카카오뱅크', NOW(), 'admin', NOW(), 'admin'),
       ('89', '케이뱅크', NOW(), 'admin', NOW(), 'admin'),
       ('92', '토스뱅크', NOW(), 'admin', NOW(), 'admin'),
       ('81', '하나은행', NOW(), 'admin', NOW(), 'admin'),
       ('54', '홍콩상하이은행', NOW(), 'admin', NOW(), 'admin'),
       ('03', 'IBK기업은행', NOW(), 'admin', NOW(), 'admin'),
       ('06', 'KB국민은행', NOW(), 'admin', NOW(), 'admin'),
       ('31', 'DGB대구은행', NOW(), 'admin', NOW(), 'admin'),
       ('02', 'KDB산업은행', NOW(), 'admin', NOW(), 'admin'),
       ('11', 'NH농협은행', NOW(), 'admin', NOW(), 'admin'),
       ('23', 'SC제일은행', NOW(), 'admin', NOW(), 'admin'),
       ('07', 'Sh수협은행', NOW(), 'admin', NOW(), 'admin');

