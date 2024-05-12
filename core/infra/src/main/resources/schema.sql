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
    provider           varchar(255),
    roles              varchar(255),
    updated_at         datetime(6),
    updated_by         varchar(255),
    created_at         datetime(6),
    created_by         varchar(255),
    registration_token varchar(255)
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
    heart_count        int,
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
    reservation_time datetime(6),
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
    review_id    BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT NOT NULL,
    store_id     BIGINT NOT NULL,
    rating_count DOUBLE NOT NULL,
    content      TEXT,
    image_url    VARCHAR(255),
    updated_at   datetime(6),
    updated_by   varchar(255),
    created_at   datetime(6),
    created_by   varchar(255)
);

CREATE TABLE BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL
);

CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID BIGINT PRIMARY KEY,
    VERSION          BIGINT,
    JOB_INSTANCE_ID  BIGINT    NOT NULL,
    CREATE_TIME      TIMESTAMP NOT NULL,
    START_TIME       TIMESTAMP DEFAULT NULL,
    END_TIME         TIMESTAMP DEFAULT NULL,
    STATUS           VARCHAR(10),
    EXIT_CODE        VARCHAR(20),
    EXIT_MESSAGE     VARCHAR(2500),
    LAST_UPDATED     TIMESTAMP,
    constraint JOB_INSTANCE_EXECUTION_FK foreign key (JOB_INSTANCE_ID)
        references BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    PARAMETER_NAME   VARCHAR(100) NOT NULL,
    PARAMETER_TYPE   VARCHAR(100) NOT NULL,
    PARAMETER_VALUE  VARCHAR(2500),
    IDENTIFYING      CHAR(1)      NOT NULL,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);


CREATE TABLE BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        TIMESTAMP    NOT NULL,
    START_TIME         TIMESTAMP DEFAULT NULL,
    END_TIME           TIMESTAMP DEFAULT NULL,
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(20),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       TIMESTAMP,
    constraint JOB_EXECUTION_STEP_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT LONGTEXT,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT LONGTEXT,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
        references BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_SEQ
(
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_SEQ
(
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_SEQ
(
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY
) ENGINE = InnoDB;


INSERT INTO BATCH_STEP_EXECUTION_SEQ
values (0);
INSERT INTO BATCH_JOB_EXECUTION_SEQ
values (0);
INSERT INTO BATCH_JOB_SEQ
values (0);



