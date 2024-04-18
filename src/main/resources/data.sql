INSERT INTO customer (created_at, social_id, updated_at, active_status, email, phone_number, role, user_name)
 VALUES ('2024-04-18 12:00:00', 1234567890, '2024-04-18 12:00:00', 'ACTIVATED', 'customer1@example.com', '123-456-7890', 'USER', 'customer1');

INSERT INTO manager (created_at, social_id, updated_at, active_status, email, phone_number, role, user_name)
VALUES ('2024-04-18 12:00:00', 1234567891, '2024-04-18 12:00:00', 'ACTIVATED', 'manager1@example.com', '123-456-7890', 'MANAGER', 'manager1');

INSERT INTO organization (seat_count, created_at, manager_id, updated_at, active_status, introduce, name)
VALUES (8, '2024-04-18 12:00:00', 1, '2024-04-18 12:00:00', 'ACTIVATED', '어쩌구 저쩌구', '어쩌구 주점');

INSERT INTO waiting (head_count, table_number, waiting_number, created_at, customer_id, entrance_time, organization_id, updated_at, active_status, entrance_status)
VALUES (4, null, 1, '2024-04-18 12:00:00', 1, null, 1, '2024-04-18 12:00:00', 'ACTIVATED', 'WAITING');

