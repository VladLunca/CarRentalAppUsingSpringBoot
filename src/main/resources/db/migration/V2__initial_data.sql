-- ============================================================
-- DEMO DATA
-- ============================================================
-- Credentials (password: admin)
--   manager1     / admin  -- MANAGER  @ AutoRent SRL
--   employee1    / admin  -- EMPLOYEE @ AutoRent SRL
--   customer1    / admin  -- CUSTOMER
-- ============================================================

-- ============================================================
-- COMPANY
-- ============================================================
INSERT INTO address (city_name, street_name, street_number)
VALUES ('Bucuresti', 'Calea Victoriei', 10);
SET @company_address_id = LAST_INSERT_ID();

INSERT INTO car_rental_company (car_rental_company_name, address_id, phone_number, email, description)
VALUES ('AutoRent SRL', @company_address_id, '0721000000', 'contact@autorent.ro', 'Servicii de inchirieri auto premium in Bucuresti.');
SET @company_id = LAST_INSERT_ID();


-- ============================================================
-- ACCOUNTS
-- ============================================================

-- Manager
INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
VALUES ('Ion', 'Popescu', '0711000001', 'manager@autorent.ro', '1850101410011');
SET @manager_details_id = LAST_INSERT_ID();

INSERT INTO users (username, password, user_details_id, enabled)
VALUES ('manager1', '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS', @manager_details_id, TRUE);
SET @manager_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role, car_rental_company_id)
VALUES (@manager_id, 'MANAGER', @company_id);

-- Employee
INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
VALUES ('Maria', 'Ionescu', '0711000002', 'employee@autorent.ro', '2860201410011');
SET @employee_details_id = LAST_INSERT_ID();

INSERT INTO users (username, password, user_details_id, enabled)
VALUES ('employee1', '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS', @employee_details_id, TRUE);
SET @employee_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role, car_rental_company_id)
VALUES (@employee_id, 'EMPLOYEE', @company_id);

-- Customer
INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
VALUES ('Andrei', 'Constantin', '0711000003', 'customer@demo.ro', '1900315410011');
SET @customer_details_id = LAST_INSERT_ID();

INSERT INTO users (username, password, user_details_id, enabled)
VALUES ('customer1', '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS', @customer_details_id, TRUE);
SET @customer_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role, car_rental_company_id)
VALUES (@customer_id, 'CUSTOMER', NULL);


-- ============================================================
-- ENGINES
-- ============================================================
INSERT INTO engine (engine_type, horse_power, capacity) VALUES ('GASOLINE', 150, 1.6);
SET @engine_gasoline_id = LAST_INSERT_ID();

INSERT INTO engine (engine_type, horse_power, capacity) VALUES ('DIESEL', 200, 2.0);
SET @engine_diesel_id = LAST_INSERT_ID();

INSERT INTO engine (engine_type, horse_power, capacity) VALUES ('ELECTRIC', 204, 0);
SET @engine_electric_id = LAST_INSERT_ID();


-- ============================================================
-- TRANSMISSIONS
-- ============================================================
INSERT INTO transmission (transmission_type, transmission_name, number_of_gears)
VALUES ('MANUAL', 'Manuala 6 trepte', 6);
SET @trans_manual_id = LAST_INSERT_ID();

INSERT INTO transmission (transmission_type, transmission_name, number_of_gears)
VALUES ('AUTOMATIC', 'Automata 7 trepte', 7);
SET @trans_auto_id = LAST_INSERT_ID();


-- ============================================================
-- CAR_BODIES
-- ============================================================
INSERT INTO car_body (name, number_of_seats, number_of_doors) VALUES ('SEDAN', 5, 4);
SET @body_sedan_id = LAST_INSERT_ID();

INSERT INTO car_body (name, number_of_seats, number_of_doors) VALUES ('SUV', 5, 5);
SET @body_suv_id = LAST_INSERT_ID();

INSERT INTO car_body (name, number_of_seats, number_of_doors) VALUES ('HATCHBACK', 5, 5);
SET @body_hatchback_id = LAST_INSERT_ID();


-- ============================================================
-- CATEGORIES
-- ============================================================
INSERT INTO category (category_name, category_description)
VALUES ('Economy', 'Masini accesibile, potrivite pentru deplasari urbane.');
SET @cat_economy_id = LAST_INSERT_ID();

INSERT INTO category (category_name, category_description)
VALUES ('Business', 'Confort si dotari premium pentru calatorii de afaceri.');
SET @cat_business_id = LAST_INSERT_ID();

INSERT INTO category (category_name, category_description)
VALUES ('SUV', 'Spatiu generos si tractiune superioara.');
SET @cat_suv_id = LAST_INSERT_ID();


-- ============================================================
-- CAR_MODELS
-- ============================================================
INSERT INTO car_model (brand, model, year, engine_id, car_body_id, transmission_id, category_id,
                       traction, fuel_consumption, number_of_luggage, price_per_day)
VALUES ('Dacia', 'Logan', 2022, @engine_gasoline_id, @body_sedan_id, @trans_manual_id, @cat_economy_id,
        'FWD', 6.5, 2, 120);
SET @model_logan_id = LAST_INSERT_ID();

INSERT INTO car_model (brand, model, year, engine_id, car_body_id, transmission_id, category_id,
                       traction, fuel_consumption, number_of_luggage, price_per_day)
VALUES ('Volkswagen', 'Golf', 2023, @engine_gasoline_id, @body_hatchback_id, @trans_auto_id, @cat_economy_id,
        'FWD', 7.0, 2, 160);
SET @model_golf_id = LAST_INSERT_ID();

INSERT INTO car_model (brand, model, year, engine_id, car_body_id, transmission_id, category_id,
                       traction, fuel_consumption, number_of_luggage, price_per_day)
VALUES ('BMW', 'X5', 2023, @engine_diesel_id, @body_suv_id, @trans_auto_id, @cat_suv_id,
        'AWD', 8.5, 3, 350);
SET @model_bmw_id = LAST_INSERT_ID();

INSERT INTO car_model (brand, model, year, engine_id, car_body_id, transmission_id, category_id,
                       traction, fuel_consumption, number_of_luggage, price_per_day)
VALUES ('Tesla', 'Model 3', 2024, @engine_electric_id, @body_sedan_id, @trans_auto_id, @cat_business_id,
        'AWD', 0, 2, 280);
SET @model_tesla_id = LAST_INSERT_ID();


-- ============================================================
-- CARS
-- ============================================================
INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
VALUES (@model_logan_id, @company_id, 'B123LOG', 'Alb', 15000, 'AVAILABLE');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
VALUES (@model_logan_id, @company_id, 'B456LOG', 'Gri', 22000, 'AVAILABLE');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
VALUES (@model_golf_id, @company_id, 'B789GOL', 'Negru', 8000, 'AVAILABLE');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
VALUES (@model_golf_id, @company_id, 'BV12GOL', 'Albastru', 31000, 'AVAILABLE');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
VALUES (@model_bmw_id, @company_id, 'BV34BMW', 'Negru', 5000, 'AVAILABLE');

INSERT INTO car (car_model_id, car_rental_company_id, licence_plate, color, mileage, status)
VALUES (@model_tesla_id, @company_id, 'IS12TSL', 'Alb', 2000, 'AVAILABLE');
