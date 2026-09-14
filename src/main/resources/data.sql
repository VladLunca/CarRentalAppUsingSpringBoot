-- ============================================================
-- REQUIRED SEED + DEMO DATA
-- Superseded by the Flyway migrations in db/migration.
-- Kept for reference; not executed while spring.sql.init.mode is disabled.
-- All inserts are idempotent (WHERE NOT EXISTS)
-- ============================================================
-- Credentials (password: admin)
--   admin     / admin  — SUPER_ADMIN
--   manager1  / admin  — MANAGER  @ AutoRent SRL
--   employee1 / admin  — EMPLOYEE @ AutoRent SRL
--   customer1 / admin  — CUSTOMER
-- ============================================================


-- ============================================================
-- SUPER ADMIN
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


-- ============================================================
-- DEMO COMPANY
-- ============================================================
INSERT INTO address (city_name, street_name, street_number)
SELECT 'Bucuresti', 'Calea Victoriei', 10
WHERE NOT EXISTS (SELECT 1 FROM address
                  WHERE city_name = 'Bucuresti'
                    AND street_name = 'Calea Victoriei'
                    AND street_number = 10);

INSERT INTO car_rental_company (car_rental_company_name, address_id, phone_number, email, description)
SELECT 'AutoRent SRL',
       (SELECT address_id FROM address WHERE city_name = 'Bucuresti' AND street_name = 'Calea Victoriei' AND street_number = 10),
       '0721000000', 'contact@autorent.ro', 'Servicii de inchirieri auto premium in Bucuresti.'
WHERE NOT EXISTS (SELECT 1 FROM car_rental_company WHERE email = 'contact@autorent.ro');


-- ============================================================
-- DEMO ACCOUNTS
-- ============================================================

-- Manager
INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
SELECT 'Ion', 'Popescu', '0711000001', 'manager@autorent.ro', '1850101410011'
WHERE NOT EXISTS (SELECT 1 FROM user_details WHERE email = 'manager@autorent.ro');

INSERT INTO users (username, password, user_details_id, enabled)
SELECT 'manager1',
       '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS',
       (SELECT user_details_id FROM user_details WHERE email = 'manager@autorent.ro'),
       TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'manager1');

INSERT INTO user_roles (user_id, role, car_rental_company_id)
SELECT (SELECT user_id FROM users WHERE username = 'manager1'),
       'MANAGER',
       (SELECT car_rental_company_id FROM car_rental_company WHERE email = 'contact@autorent.ro')
WHERE NOT EXISTS (SELECT 1 FROM user_roles
                  WHERE user_id = (SELECT user_id FROM users WHERE username = 'manager1')
                    AND role = 'MANAGER');

-- Employee
INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
SELECT 'Maria', 'Ionescu', '0711000002', 'employee@autorent.ro', '2860201410011'
WHERE NOT EXISTS (SELECT 1 FROM user_details WHERE email = 'employee@autorent.ro');

INSERT INTO users (username, password, user_details_id, enabled)
SELECT 'employee1',
       '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS',
       (SELECT user_details_id FROM user_details WHERE email = 'employee@autorent.ro'),
       TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'employee1');

INSERT INTO user_roles (user_id, role, car_rental_company_id)
SELECT (SELECT user_id FROM users WHERE username = 'employee1'),
       'EMPLOYEE',
       (SELECT car_rental_company_id FROM car_rental_company WHERE email = 'contact@autorent.ro')
WHERE NOT EXISTS (SELECT 1 FROM user_roles
                  WHERE user_id = (SELECT user_id FROM users WHERE username = 'employee1')
                    AND role = 'EMPLOYEE');

-- Customer
INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
SELECT 'Andrei', 'Constantin', '0711000003', 'customer@demo.ro', '1900315410011'
WHERE NOT EXISTS (SELECT 1 FROM user_details WHERE email = 'customer@demo.ro');

INSERT INTO users (username, password, user_details_id, enabled)
SELECT 'customer1',
       '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS',
       (SELECT user_details_id FROM user_details WHERE email = 'customer@demo.ro'),
       TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'customer1');

INSERT INTO user_roles (user_id, role, car_rental_company_id)
SELECT (SELECT user_id FROM users WHERE username = 'customer1'), 'CUSTOMER', NULL
WHERE NOT EXISTS (SELECT 1 FROM user_roles
                  WHERE user_id = (SELECT user_id FROM users WHERE username = 'customer1')
                    AND role = 'CUSTOMER');


-- ============================================================
-- ENGINES
-- ============================================================
INSERT INTO engine (engine_type, horse_power, capacity)
SELECT 'GASOLINE', 150, 1.6
WHERE NOT EXISTS (SELECT 1 FROM engine WHERE engine_type = 'GASOLINE' AND horse_power = 150);

INSERT INTO engine (engine_type, horse_power, capacity)
SELECT 'DIESEL', 200, 2.0
WHERE NOT EXISTS (SELECT 1 FROM engine WHERE engine_type = 'DIESEL' AND horse_power = 200);

INSERT INTO engine (engine_type, horse_power, capacity)
SELECT 'ELECTRIC', 204, 0
WHERE NOT EXISTS (SELECT 1 FROM engine WHERE engine_type = 'ELECTRIC' AND horse_power = 204);


-- ============================================================
-- CAR_BODIES
-- ============================================================
INSERT INTO car_body (name, number_of_seats, number_of_doors)
SELECT 'SEDAN', 5, 4
WHERE NOT EXISTS (SELECT 1 FROM car_body WHERE name = 'SEDAN' AND number_of_seats = 5);

INSERT INTO car_body (name, number_of_seats, number_of_doors)
SELECT 'SUV', 5, 5
WHERE NOT EXISTS (SELECT 1 FROM car_body WHERE name = 'SUV' AND number_of_seats = 5);

INSERT INTO car_body (name, number_of_seats, number_of_doors)
SELECT 'HATCHBACK', 5, 5
WHERE NOT EXISTS (SELECT 1 FROM car_body WHERE name = 'HATCHBACK' AND number_of_seats = 5);


-- ============================================================
-- TRANSMISSIONS
-- ============================================================
INSERT INTO transmission (transmission_type, transmission_name, number_of_gears)
SELECT 'MANUAL', 'Manuala 6 trepte', 6
WHERE NOT EXISTS (SELECT 1 FROM transmission WHERE transmission_name = 'Manuala 6 trepte');

INSERT INTO transmission (transmission_type, transmission_name, number_of_gears)
SELECT 'AUTOMATIC', 'Automata 7 trepte', 7
WHERE NOT EXISTS (SELECT 1 FROM transmission WHERE transmission_name = 'Automata 7 trepte');


-- ============================================================
-- CATEGORIES
-- ============================================================
INSERT INTO category (category_name, category_description)
SELECT 'Economy', 'Masini accesibile, potrivite pentru deplasari urbane.'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category_name = 'Economy');

INSERT INTO category (category_name, category_description)
SELECT 'Business', 'Confort si dotari premium pentru calatorii de afaceri.'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category_name = 'Business');

INSERT INTO category (category_name, category_description)
SELECT 'SUV', 'Spatiu generos si tractiune superioara.'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category_name = 'SUV');


-- ============================================================
-- CAR_MODELS
-- ============================================================
INSERT INTO car_model (brand, model, year, engine_id, car_body_id, transmission_id, category_id,
                       traction, fuel_consumption, number_of_luggage, price_per_day)
SELECT 'Dacia', 'Logan', 2022,
       (SELECT engine_id FROM engine WHERE engine_type = 'GASOLINE' AND horse_power = 150),
       (SELECT car_body_id FROM car_body WHERE name = 'SEDAN' AND number_of_seats = 5),
       (SELECT transmission_id FROM transmission WHERE transmission_name = 'Manuala 6 trepte'),
       (SELECT category_id FROM category WHERE category_name = 'Economy'),
       'FWD', 6.5, 2, 120
WHERE NOT EXISTS (SELECT 1 FROM car_model WHERE brand = 'Dacia' AND model = 'Logan' AND year = 2022);

INSERT INTO car_model (brand, model, year, engine_id, car_body_id, transmission_id, category_id,
                       traction, fuel_consumption, number_of_luggage, price_per_day)
SELECT 'Volkswagen', 'Golf', 2023,
       (SELECT engine_id FROM engine WHERE engine_type = 'GASOLINE' AND horse_power = 150),
       (SELECT car_body_id FROM car_body WHERE name = 'HATCHBACK' AND number_of_seats = 5),
       (SELECT transmission_id FROM transmission WHERE transmission_name = 'Automata 7 trepte'),
       (SELECT category_id FROM category WHERE category_name = 'Economy'),
       'FWD', 7.0, 2, 160
WHERE NOT EXISTS (SELECT 1 FROM car_model WHERE brand = 'Volkswagen' AND model = 'Golf' AND year = 2023);

INSERT INTO car_model (brand, model, year, engine_id, car_body_id, transmission_id, category_id,
                       traction, fuel_consumption, number_of_luggage, price_per_day)
SELECT 'BMW', 'X5', 2023,
       (SELECT engine_id FROM engine WHERE engine_type = 'DIESEL' AND horse_power = 200),
       (SELECT car_body_id FROM car_body WHERE name = 'SUV' AND number_of_seats = 5),
       (SELECT transmission_id FROM transmission WHERE transmission_name = 'Automata 7 trepte'),
       (SELECT category_id FROM category WHERE category_name = 'SUV'),
       'AWD', 8.5, 3, 350
WHERE NOT EXISTS (SELECT 1 FROM car_model WHERE brand = 'BMW' AND model = 'X5' AND year = 2023);

INSERT INTO car_model (brand, model, year, engine_id, car_body_id, transmission_id, category_id,
                       traction, fuel_consumption, number_of_luggage, price_per_day)
SELECT 'Tesla', 'Model 3', 2024,
       (SELECT engine_id FROM engine WHERE engine_type = 'ELECTRIC' AND horse_power = 204),
       (SELECT car_body_id FROM car_body WHERE name = 'SEDAN' AND number_of_seats = 5),
       (SELECT transmission_id FROM transmission WHERE transmission_name = 'Automata 7 trepte'),
       (SELECT category_id FROM category WHERE category_name = 'Business'),
       'AWD', 0, 2, 280
WHERE NOT EXISTS (SELECT 1 FROM car_model WHERE brand = 'Tesla' AND model = 'Model 3' AND year = 2024);


-- ============================================================
-- CARS
-- ============================================================
INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
SELECT (SELECT car_model_id FROM car_model WHERE brand = 'Dacia' AND model = 'Logan' AND year = 2022),
       (SELECT car_rental_company_id FROM car_rental_company WHERE email = 'contact@autorent.ro'),
       'B123LOG', 'Alb', 15000, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM car WHERE licence_plate = 'B123LOG');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
SELECT (SELECT car_model_id FROM car_model WHERE brand = 'Dacia' AND model = 'Logan' AND year = 2022),
       (SELECT car_rental_company_id FROM car_rental_company WHERE email = 'contact@autorent.ro'),
       'B456LOG', 'Gri', 22000, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM car WHERE licence_plate = 'B456LOG');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
SELECT (SELECT car_model_id FROM car_model WHERE brand = 'Volkswagen' AND model = 'Golf' AND year = 2023),
       (SELECT car_rental_company_id FROM car_rental_company WHERE email = 'contact@autorent.ro'),
       'B789GOL', 'Negru', 8000, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM car WHERE licence_plate = 'B789GOL');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
SELECT (SELECT car_model_id FROM car_model WHERE brand = 'Volkswagen' AND model = 'Golf' AND year = 2023),
       (SELECT car_rental_company_id FROM car_rental_company WHERE email = 'contact@autorent.ro'),
       'BV12GOL', 'Albastru', 31000, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM car WHERE licence_plate = 'BV12GOL');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
SELECT (SELECT car_model_id FROM car_model WHERE brand = 'BMW' AND model = 'X5' AND year = 2023),
       (SELECT car_rental_company_id FROM car_rental_company WHERE email = 'contact@autorent.ro'),
       'BV34BMW', 'Negru', 5000, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM car WHERE licence_plate = 'BV34BMW');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
SELECT (SELECT car_model_id FROM car_model WHERE brand = 'Tesla' AND model = 'Model 3' AND year = 2024),
       (SELECT car_rental_company_id FROM car_rental_company WHERE email = 'contact@autorent.ro'),
       'IS12TSL', 'Alb', 2000, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM car WHERE licence_plate = 'IS12TSL');
