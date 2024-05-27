INSERT INTO project (name, description, parent_project_id, storage_size, max_storage_size,  owner_id, created_at, updated_at, status, visibility, cover_image_id)
VALUES
    ('Yandex', 'российская транснациональная компания в отрасли информационных технологий', null, 100000, 100000, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'IN_PROGRESS', 'PUBLIC', null),
    ('Yandex-Taxi', 'одна из самостоятельных бизнес-единиц «Яндекса»', 1, 100000, 100000, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'IN_PROGRESS', 'PUBLIC', null),
    ('MichaelJohnson', 'michaeljohnson@example.com', '1112223333', 'password3', true, 'About Michael Johnson', 1, 'Sydney', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('EmilyDavis', 'emilydavis@example.com', '4445556666', 'password4', true, 'About Emily Davis', 3, 'Paris', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('WilliamTaylor', 'williamtaylor@example.com', '7778889999', 'password5', true, 'About William Taylor', 2, 'Toronto', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('OliviaAnderson', 'oliviaanderson@example.com', '0001112222', 'password6', true, 'About Olivia Anderson', 1, 'Berlin', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JamesWilson', 'jameswilson@example.com', '3334445555', 'password7', true, 'About James Wilson', 3, 'Tokyo', 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SophiaMartin', 'sophiamartin@example.com', '6667778888', 'password8', true, 'About Sophia Martin', 4, 'Rome', 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('BenjaminThompson', 'benjaminthompson@example.com', '9990001111', 'password9', true, 'About Benjamin Thompson', 4, 'Moscow', 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AvaHarris', 'avaharris@example.com', '2223334444', 'password10', true, 'About Ava Harris', 3, 'Madrid', 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);