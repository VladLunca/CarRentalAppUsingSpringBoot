INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
SELECT 'Super', 'Admin', '0700000000', 'admin@gmail.com', '1900101410011'
WHERE NOT EXISTS (SELECT 1 FROM user_details WHERE email = 'admin@gmail.com');

INSERT INTO users (username, password, user_details_id, enabled)
SELECT 'admin', '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS',
       (SELECT user_details_id FROM user_details WHERE email = 'admin@gmail.com'), TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO user_roles (user_id, role, car_rental_company_id)
SELECT user_id, 'SUPER_ADMIN', NULL FROM users WHERE username = 'admin'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT user_id FROM users WHERE username = 'admin'));
