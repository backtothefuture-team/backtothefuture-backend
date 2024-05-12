INSERT INTO member (member_id, auth_id, email, name, password, phone_number, status, provider, roles, updated_at,
                    updated_by, created_at, created_by)
VALUES (1, null, 'email@naver.com', '이상민', 'mmsc532mmmm', '010-0000-0000', 'ACTIVE', null, 'ROLE_STORE_OWNER', null,
        null,
        null, null);

INSERT INTO store (name, description, location, contact, image, member_id, updated_at, updated_by, created_at, created_by, average_rating, total_rating_count, start_time, end_time, sorting_index, latitude, longitude)
VALUES
('상점1', '상점1 - 설명', 'Location 1', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:00:00', '18:00:00', 50000000001, 37.41152, 127.1285),
('상점2', '상점2 - 설명', 'Location 2', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 4, 10, '10:00:00', '19:00:00', 40000000002, 37.39581, 127.1283),
('상점3', '상점3 - 설명', 'Location 3', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 3.5, 10, '08:00:00', '17:00:00', 35000000003, 37.37923, 127.1143),
('상점4', '상점4 - 설명', 'Location 4', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 2, 10, '09:30:00', '18:30:00', 20000000004, 37.36621, 127.108),
('상점5', '상점5 - 설명', 'Location 5', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 1, 10, '10:30:00', '19:30:00', 10000000005, 37.35075, 127.1089),
('상점6', '상점6 - 설명', 'Location 6', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '08:30:00', '17:30:00', 50000000006, 37.33985, 127.1090),
('상점7', '상점7 - 설명', 'Location 7', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 4.5, 10, '09:00:00', '18:00:00', 45000000007, 37.50507, 127.0044),
('상점8', '상점8 - 설명', 'Location 8', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 3, 10, '10:00:00', '19:00:00', 30000000008, 37.50348, 126.9961),
('상점9', '상점9 - 설명', 'Location 9', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 2, 10, '08:00:00', '17:00:00', 20000000009, 37.50176, 126.9875),
('상점10', '상점10 - 설명', 'Location 10', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 1, 10, '09:30:00', '18:30:00', 10000000010, 37.50290, 126.9803),
('상점11', '상점11 - 설명', 'Location 11', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '10:30:00', '19:30:00', 50000000011, 37.50918, 126.9634),
('상점12', '상점12 - 설명', 'Location 12', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 4, 10, '08:30:00', '17:30:00', 40000000012, 37.51300, 126.9532),
('상점13', '상점13 - 설명', 'Location 13', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 3, 10, '09:00:00', '18:00:00', 30000000013, 37.51412, 126.9417),
('상점14', '상점14 - 설명', 'Location 14', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 2.5, 10, '10:00:00', '19:00:00', 25000000014, 37.51375, 126.9264),
('상점15', '상점15 - 설명', 'Location 15', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 1, 10, '08:00:00', '17:00:00', 10000000015, 37.51702, 126.9178),
('상점16', '상점16 - 설명', 'Location 16', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 5, 10, '09:30:00', '18:30:00', 500000000016, 37.51547, 126.9071),
('상점17', '상점17 - 설명', 'Location 17', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 4, 10, '10:30:00', '19:30:00', 40000000017, 37.50914, 126.8893),
('상점18', '상점18 - 설명', 'Location 18', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 3, 10, '08:30:00', '17:30:00', 30000000018, 37.50357, 126.8823),
('상점19', '상점19 - 설명', 'Location 19', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 2, 10, '09:00:00', '18:00:00', 20000000019, 37.49452, 126.8591),
('상점20', '상점20 - 설명', 'Location 20', '010-1234-1234', 'https://bagtothefuture-static-image.s3.ap-northeast-2.amazonaws.com/review/4/20240502014849.', 1, NULL, NULL, NULL, NULL, 1.5, 10, '10:00:00', '19:00:00', 15000000020, 37.47646, 126.6169);

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
