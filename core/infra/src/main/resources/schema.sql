DROP DATABASE IF EXISTS test;
CREATE DATABASE IF NOT EXISTS test;
USE test;

CREATE TABLE member (
                        member_id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        auth_id varchar(255),
                        email varchar(255) UNIQUE,
                        name varchar(255),
                        password varchar(255),
                        phone_number varchar(255) UNIQUE,
                        status varchar(255),
                        provider varchar(255),
                        roles varchar(255),
                        updated_at datetime(6),
                        updated_by varchar(255),
                        created_at datetime(6),
                        created_by varchar(255)
);


CREATE TABLE store (
                       store_id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       name varchar(255),
                       description TEXT,
                       location varchar(255),
                       contact varchar(255),
                       image varchar(255),
                       member_id bigint NOT NULL,
                       updated_at datetime(6),
                       updated_by varchar(255),
                       created_at datetime(6),
                       created_by varchar(255),
                       FOREIGN KEY(member_id) REFERENCES member(member_id)
);

CREATE TABLE product (
                         product_id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         name varchar(255),
                         description TEXT,
                         price int,
                         stock_quantity int,
                         thumbnail varchar(255),
                         store_id bigint NOT NULL,
                         updated_at datetime(6),
                         updated_by varchar(255),
                         created_at datetime(6),
                         created_by varchar(255),
                         FOREIGN KEY(store_id) REFERENCES store(store_id)
);

