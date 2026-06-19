-- ============================================================
-- CAR RENTAL MARKETPLACE
-- Database tables
-- ============================================================


-- ============================================================
-- ADRESE
-- ============================================================
CREATE TABLE address (
  address_id    INT NOT NULL AUTO_INCREMENT,
  city_name     VARCHAR(50) NOT NULL,
  street_name   VARCHAR(50) NOT NULL,
  street_number INT NOT NULL,
  PRIMARY KEY (address_id),
  CONSTRAINT street_number_valid CHECK (street_number > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- FIRME
-- ============================================================
CREATE TABLE car_rental_company (
  car_rental_company_id   INT NOT NULL AUTO_INCREMENT,
  car_rental_company_name VARCHAR(50) NOT NULL,
  address_id              INT NOT NULL,
  phone_number            CHAR(10) NOT NULL,
  email                   VARCHAR(68) NOT NULL,
  description             VARCHAR(255),
  PRIMARY KEY (car_rental_company_id),
  CONSTRAINT uq_company_email          UNIQUE (email),
  CONSTRAINT company_phone_only_digits CHECK (phone_number REGEXP '^[0-9]{10}$'),
  CONSTRAINT company_address_fk        FOREIGN KEY (address_id)
    REFERENCES address(address_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- DATE PERSONALE
-- ============================================================
CREATE TABLE user_details (
  user_details_id INT NOT NULL AUTO_INCREMENT,
  first_name      VARCHAR(68) NOT NULL,
  last_name       VARCHAR(68) NOT NULL,
  phone_number    CHAR(10) NOT NULL,
  email           VARCHAR(68) NOT NULL,
  CNP             CHAR(13) NOT NULL,
  PRIMARY KEY (user_details_id),
  CONSTRAINT uq_email          UNIQUE (email),
  CONSTRAINT uq_cnp            UNIQUE (CNP),
  CONSTRAINT cnp_only_digits   CHECK (CNP REGEXP '^[0-9]{13}$'),
  CONSTRAINT phone_only_digits CHECK (phone_number REGEXP '^[0-9]{10}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- CONTURI
-- ============================================================
CREATE TABLE users (
  user_id         INT NOT NULL AUTO_INCREMENT,
  username        VARCHAR(20) NOT NULL,
  password        CHAR(68) NOT NULL,
  user_details_id INT NOT NULL,
  enabled         BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (user_id),
  CONSTRAINT uq_username      UNIQUE (username),
  CONSTRAINT uq_user_details  UNIQUE (user_details_id),
  CONSTRAINT users_details_fk FOREIGN KEY (user_details_id)
    REFERENCES user_details(user_details_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- ROLURI
-- ============================================================
CREATE TABLE user_roles (
  user_role_id          INT NOT NULL AUTO_INCREMENT,
  user_id               INT NOT NULL,
  role                  VARCHAR(15) NOT NULL,
  car_rental_company_id INT,
  PRIMARY KEY (user_role_id),
  CONSTRAINT role_valid CHECK (
    role IN ('CUSTOMER', 'EMPLOYEE', 'MANAGER', 'SUPER_ADMIN')
  ),
  CONSTRAINT super_admin_no_company CHECK (
    role != 'SUPER_ADMIN' OR car_rental_company_id IS NULL
  ),
  CONSTRAINT company_required_for_staff CHECK (
    role NOT IN ('EMPLOYEE', 'MANAGER') OR car_rental_company_id IS NOT NULL
  ),
  CONSTRAINT customer_no_company CHECK (
    role != 'CUSTOMER' OR car_rental_company_id IS NULL
  ),
  CONSTRAINT uq_user_role_company  UNIQUE (user_id, role, car_rental_company_id),
  CONSTRAINT user_roles_user_fk    FOREIGN KEY (user_id)
    REFERENCES users(user_id) ON DELETE CASCADE,
  CONSTRAINT user_roles_company_fk FOREIGN KEY (car_rental_company_id)
    REFERENCES car_rental_company(car_rental_company_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- MOTOR
-- ============================================================
CREATE TABLE engine (
  engine_id   INT NOT NULL AUTO_INCREMENT,
  engine_type VARCHAR(8) NOT NULL,
  horse_power INT NOT NULL,
  capacity    FLOAT,
  PRIMARY KEY (engine_id),
  CONSTRAINT engine_type_valid CHECK (engine_type IN ('GASOLINE', 'DIESEL', 'HYBRID', 'ELECTRIC','GPL')),
  CONSTRAINT horse_power_valid CHECK (horse_power > 0),
  CONSTRAINT capacity_valid    CHECK (capacity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- CAROSERIE
-- ============================================================
CREATE TABLE car_body (
  car_body_id     INT NOT NULL AUTO_INCREMENT,
  name            VARCHAR(10) NOT NULL,
  number_of_seats INT NOT NULL,
  number_of_doors INT NOT NULL,
  PRIMARY KEY (car_body_id),
  CONSTRAINT car_body_name_valid   CHECK (name IN ('SEDAN','SUV','CROSSOVER','HATCHBACK','COUPE','FASTBACK','ROADSTER')),
  CONSTRAINT number_of_seats_valid CHECK (number_of_seats > 1),
  CONSTRAINT number_of_doors_valid CHECK (number_of_doors >= 2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- TRANSMISIE
-- ============================================================
CREATE TABLE transmission (
  transmission_id   INT NOT NULL AUTO_INCREMENT,
  transmission_type VARCHAR(9) NOT NULL,
  transmission_name VARCHAR(30) NOT NULL,
  number_of_gears   INT NOT NULL,
  PRIMARY KEY (transmission_id),
  CONSTRAINT transmission_type_valid CHECK (UPPER(transmission_type) IN ('AUTOMATIC', 'MANUAL')),
  CONSTRAINT number_of_gears_valid   CHECK (number_of_gears > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- CATEGORIE MASINA (economic, business, premium etc.)
-- ============================================================
CREATE TABLE category (
  category_id          INT NOT NULL AUTO_INCREMENT,
  category_name        VARCHAR(50) NOT NULL,
  category_description VARCHAR(100) NOT NULL,
  PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- MODEL MASINA (global pe platforma)
-- ============================================================
CREATE TABLE car_model (
  car_model_id      INT NOT NULL AUTO_INCREMENT,
  brand             VARCHAR(20) NOT NULL,
  model             VARCHAR(20) NOT NULL,
  year              INT NOT NULL,
  engine_id         INT NOT NULL,
  car_body_id       INT NOT NULL,
  transmission_id   INT NOT NULL,
  category_id       INT NOT NULL,
  traction          VARCHAR(5) NOT NULL,
  fuel_consumption  FLOAT NOT NULL,
  number_of_luggage INT NOT NULL,
  price_per_day     INT NOT NULL,
  PRIMARY KEY (car_model_id),
  CONSTRAINT uq_car_model            UNIQUE (brand, model, year, engine_id, transmission_id),
  CONSTRAINT traction_valid          CHECK (traction IN ('FWD', 'RWD', '4WD', 'AWD')),
  CONSTRAINT fuel_consumption_valid  CHECK (fuel_consumption >= 0),
  CONSTRAINT number_of_luggage_valid CHECK (number_of_luggage >= 0),
  CONSTRAINT price_per_day_valid     CHECK (price_per_day > 0),
  CONSTRAINT year_valid              CHECK (year >= 1886),
  CONSTRAINT car_model_engine_fk       FOREIGN KEY (engine_id)
    REFERENCES engine(engine_id) ON DELETE RESTRICT,
  CONSTRAINT car_model_car_body_fk     FOREIGN KEY (car_body_id)
    REFERENCES car_body(car_body_id) ON DELETE RESTRICT,
  CONSTRAINT car_model_transmission_fk FOREIGN KEY (transmission_id)
    REFERENCES transmission(transmission_id) ON DELETE RESTRICT,
  CONSTRAINT car_model_category_fk     FOREIGN KEY (category_id)
    REFERENCES category(category_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- MASINA FIZICA (instanta per firma)
-- ============================================================
CREATE TABLE car (
  car_id                INT NOT NULL AUTO_INCREMENT,
  car_model_id          INT NOT NULL,
  car_rental_company_id INT NOT NULL,
  licence_plate         CHAR(7) NOT NULL,
  color                 VARCHAR(20) NOT NULL,
  mileage               INT NOT NULL,
  status                VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE',
  image                 VARCHAR(100),
  PRIMARY KEY (car_id),
  CONSTRAINT uq_licence_plate UNIQUE (licence_plate),
  CONSTRAINT status_valid     CHECK (status IN ('AVAILABLE', 'RENTED', 'IN_SERVICE')),
  CONSTRAINT mileage_valid    CHECK (mileage >= 0),
  CONSTRAINT car_model_fk     FOREIGN KEY (car_model_id)
    REFERENCES car_model(car_model_id) ON DELETE RESTRICT,
  CONSTRAINT car_company_fk   FOREIGN KEY (car_rental_company_id)
    REFERENCES car_rental_company(car_rental_company_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- PUNCTE DE RIDICARE/PREDARE ALE FIRMEI
-- ============================================================
CREATE TABLE rental_location (
  rental_location_id    INT NOT NULL AUTO_INCREMENT,
  car_rental_company_id INT NOT NULL,
  address_id            INT NOT NULL,
  name                  VARCHAR(50) NOT NULL,
  phone_number          CHAR(10),
  PRIMARY KEY (rental_location_id),
  CONSTRAINT rental_location_company_fk FOREIGN KEY (car_rental_company_id)
    REFERENCES car_rental_company(car_rental_company_id) ON DELETE CASCADE,
  CONSTRAINT rental_location_address_fk FOREIGN KEY (address_id)
    REFERENCES address(address_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- REZERVARE
-- ============================================================
CREATE TABLE rental (
  rental_id           INT NOT NULL AUTO_INCREMENT,
  car_id              INT NOT NULL,
  user_id             INT NOT NULL,
  pickup_location_id  INT NOT NULL,
  dropoff_location_id INT NOT NULL,
  start_date          DATE NOT NULL,
  end_date            DATE NOT NULL,
  status              VARCHAR(10) NOT NULL DEFAULT 'PENDING',
  with_driver         BOOLEAN NOT NULL DEFAULT FALSE,
  child_seat          BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (rental_id),
  CONSTRAINT rental_dates_valid   CHECK (start_date < end_date),
  CONSTRAINT rental_status_valid  CHECK (status IN ('PENDING', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
  CONSTRAINT rental_car_fk        FOREIGN KEY (car_id)
    REFERENCES car(car_id) ON DELETE RESTRICT,
  CONSTRAINT rental_user_fk       FOREIGN KEY (user_id)
    REFERENCES users(user_id) ON DELETE RESTRICT,
  CONSTRAINT rental_pickup_fk     FOREIGN KEY (pickup_location_id)
    REFERENCES rental_location(rental_location_id) ON DELETE RESTRICT,
  CONSTRAINT rental_dropoff_fk    FOREIGN KEY (dropoff_location_id)
    REFERENCES rental_location(rental_location_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- Cont SUPER_ADMIN  (user: admin / parola: admin)

INSERT INTO user_details (first_name, last_name, phone_number, email, CNP)
VALUES ('Super', 'Admin', '0700000000', 'admin@gmail.com', '1900101410011');

INSERT INTO users (username, password, user_details_id, enabled)
VALUES (
           'admin',
           '{bcrypt}$2a$10$wIhMeAanfDdxbpq8iDiG5.PDFHf.G6SJay.1dr79i/XBR2CRmrHIS',
           LAST_INSERT_ID(),
           TRUE
       );

INSERT INTO user_roles (user_id, role, car_rental_company_id)
VALUES (LAST_INSERT_ID(), 'SUPER_ADMIN', NULL);

