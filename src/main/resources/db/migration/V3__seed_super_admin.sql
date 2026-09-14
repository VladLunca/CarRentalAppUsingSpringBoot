-- ============================================================
-- SUPER ADMIN BOOTSTRAP
-- ============================================================
-- The SUPER_ADMIN role can never be granted through the application:
-- StaffService.validateRoleAssignment refuses any role whose ordinal is
-- greater than or equal to the assigner's, and SUPER_ADMIN is the highest.
-- Since only a SUPER_ADMIN can promote a MANAGER, and only a MANAGER can
-- promote an EMPLOYEE, the first account has to be inserted here or the
-- whole staff side of the application stays unreachable.
--
--   admin / admin
--
-- Change this password after the first login.
-- ============================================================

INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
SELECT 'Super', 'Admin', '0700000000', 'admin@gmail.com', '1900101410011'
WHERE NOT EXISTS (SELECT 1 FROM user_details WHERE email = 'admin@gmail.com');

INSERT INTO users (username, password, user_details_id, enabled)
SELECT 'admin',
       '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS',
       (SELECT user_details_id FROM user_details WHERE email = 'admin@gmail.com'),
       TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO user_roles (user_id, role, car_rental_company_id)
SELECT (SELECT user_id FROM users WHERE username = 'admin'), 'SUPER_ADMIN', NULL
WHERE NOT EXISTS (SELECT 1 FROM user_roles
                  WHERE user_id = (SELECT user_id FROM users WHERE username = 'admin')
                    AND role = 'SUPER_ADMIN');
