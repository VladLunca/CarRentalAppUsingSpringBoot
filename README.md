# Car Rental App

A web-based car rental management system built with Spring Boot, Thymeleaf and MySQL.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.0.5, Java 21 |
| Persistence | Spring Data JPA, Hibernate, MySQL |
| Migrations | Flyway |
| Security | Spring Security 6 |
| Frontend | Thymeleaf, Bootstrap 5.2.3 |
| Utilities | Lombok |
| Containerization | Docker (multi-stage build) |

---

## Running the Application

### With Docker

```bash
docker build -t car-rental-app .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://<host>:3306/car_rental \
  -e SPRING_DATASOURCE_USERNAME=<user> \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  car-rental-app
```

### Locally

Requires Java 21 and a running MySQL instance on port 3307 with database `car_rental`.

```bash
mvn spring-boot:run
```

The app is available at `http://localhost:8080`.

---

## Project Structure

```
src/main/java/com/carreantalapp/app/
├── configurations/       # SecurityConfig, WebConfig
├── controllers/
│   ├── admin/            # Controllers accessible only by SUPER_ADMIN
│   │   ├── AdminController.java
│   │   └── CompanyController.java
│   ├── car/              # Car browsing and car model management
│   │   ├── CarBrowserController.java
│   │   └── CarModelController.java
│   │   └── components/
│   │       ├── EngineController.java
│   │       └── TransmissionController.java
│   ├── AuthController.java
│   ├── EmployeeController.java
│   ├── HomeController.java
│   ├── ManageController.java
│   └── RegistrationController.java
├── dto/                  # Data Transfer Objects
├── exceptions/           # Custom runtime exceptions
├── model/                # JPA entities
│   └── utils/            # Enums (UserRoleTypes, CarStatus, etc.)
├── repositories/         # Spring Data JPA repositories
└── services/
    ├── car/              # Car domain services
    │   ├── CarService.java
    │   ├── CarSpec.java
    │   ├── CarModelService.java
    │   └── components/
    │       ├── EngineService.java
    │       └── TransmissionService.java
    ├── CarRentalCompanyService.java
    ├── CustomUserDetailsService.java
    ├── StaffService.java
    └── UserService.java
```

---

## Domain Model

### Entities

| Entity | Description |
|---|---|
| `User` | Account with credentials and enabled flag |
| `UserDetails` | Personal info: first name, last name, email, phone, CNP |
| `UserRoles` | Role and company assignment for a user |
| `CarRentalCompany` | Company with address, phone, email, description |
| `Address` | City, street name and street number |
| `Car` | Physical vehicle instance — licence plate, color, mileage, status, image |
| `CarModel` | Make, model, year, engine, transmission, body, category, traction, price/day |
| `Engine` | Engine type (PETROL, DIESEL, etc.) and horsepower |
| `Transmission` | Transmission type (MANUAL, AUTOMATIC) and number of gears |
| `CarBody` | Body type (SEDAN, SUV, etc.) and seat/door count |
| `Category` | Car category name and description |
| `Rental` | Rental record linking user, car, dates, locations and status |
| `RentalLocation` | Pickup/dropoff location |

### User Roles

Roles are stored as the `UserRoleTypes` enum and are **independent** — there is no role hierarchy:

| Role | Description |
|---|---|
| `CUSTOMER` | Default role. Can browse available cars and create rentals. |
| `EMPLOYEE` | Company staff. Can manage company cars and rentals. |
| `MANAGER` | Company manager. Can manage staff and cars. |
| `SUPER_ADMIN` | System administrator. Can manage all accounts and companies. |

---

## Security

- Authentication via Spring Security form login (`/login`)
- Method-level authorization with `@PreAuthorize`
- No role hierarchy — each role has distinct, non-overlapping permissions
- Passwords encoded with `DelegatingPasswordEncoder`

---

## Controllers & Endpoints

### AuthController — `/login`

| Method | Path | Description |
|---|---|---|
| GET | `/login` | Show login form |

### RegistrationController — `/registration`

| Method | Path | Description |
|---|---|---|
| GET | `/registration/form` | Show registration form |
| POST | `/registration/process` | Process registration |
| GET | `/registration/confirmation` | Show confirmation page |

### AdminController — `/admin` — `SUPER_ADMIN` only

| Method | Path | Description |
|---|---|---|
| GET | `/admin/staff` | List and search all users |
| POST | `/admin/staff/assign-role` | Assign role and company to a user |
| POST | `/admin/staff/toggle-status` | Enable or disable a user account |

### CompanyController — `/admin/rentalsCompanies` — `SUPER_ADMIN` only

| Method | Path | Description |
|---|---|---|
| GET | `/admin/rentalsCompanies/show` | List and search all companies |
| GET | `/admin/rentalsCompanies/addCompany` | Show add company form |
| POST | `/admin/rentalsCompanies/addCompany` | Save new company |
| GET | `/admin/rentalsCompanies/updateCompany` | Show update form for a company |
| POST | `/admin/rentalsCompanies/processUpdateCompany` | Save company changes |
| POST | `/admin/rentalsCompanies/deleteCompany` | Delete a company |

### ManageController — `/manager` — `MANAGER` only

| Method | Path | Description |
|---|---|---|
| GET | `/manager/staff` | List company employees; search users to add |
| POST | `/manager/staff/add-employee` | Assign EMPLOYEE role to a user |
| POST | `/manager/staff/remove-employee` | Remove EMPLOYEE role from a user |

### CarBrowserController — `/cars` — `EMPLOYEE`/`MANAGER`/`CUSTOMER`

| Method | Path | Role | Description |
|---|---|---|---|
| GET | `/cars/showCars` | EMPLOYEE, MANAGER | List all company cars with filters |
| GET | `/cars/carsAvailableToRent` | CUSTOMER, EMPLOYEE, MANAGER | List available cars for a given date range with filters |

Both endpoints accept filter parameters via `CarFilterDto` (`brands`, `bodyTypes`, `categories`, `transmissions`, `tractions`, `firstYear`, `lastYear`). `/carsAvailableToRent` also accepts `start` and `end` date parameters (ISO format). Filtering is done at the database level using Spring Data Specifications (`CarSpec`).

### EmployeeController — `/employee` — `EMPLOYEE`/`MANAGER`

| Method | Path | Description |
|---|---|---|
| GET | `/employee/addCarsForm` | Show add car form |
| POST | `/employee/processCarsForm` | Save new car |
| GET | `/employee/cars/updateCar?carId=` | Show update form for a car |
| POST | `/employee/processUpdateCarsForm` | Save car changes |
| POST | `/employee/cars/toggleStatus` | Toggle car status: AVAILABLE ↔ IN_SERVICE |
| POST | `/employee/cars/deleteCar` | Delete a car (blocked if active/pending rentals exist) |

---

## Services

### `UserService`
Handles user registration and existence checks. Works with `WebUserDTO` — entities are never exposed to controllers.

### `StaffService`
Handles staff-related operations:
- Search users by username (all users, or scoped to a company — CUSTOMER + same-company EMPLOYEE)
- Assign and remove roles with hierarchy validation — no user can assign a role equal to or higher than their own
- Toggle account enabled status
- Get employees of a company
- Resolve company ID from `Authentication`
- Converts `User` entities to `UserDto` internally via `toDto()`

### `CarRentalCompanyService`
Handles company CRUD:
- Search companies by name
- Add, update and delete companies — manages the associated `Address` entity internally
- Converts `CarRentalCompany` entities to `CarRentalCompanyDto` internally via `toDto()`

### `CarService`
Handles all car operations:
- `getCompanyCars(companyId, filters)` — returns all cars of a company, filtered at DB level
- `getCarsAvailableForDates(start, end, filters)` — returns AVAILABLE cars free in the given period, filtered at DB level
- `saveCarFromDto(dto, companyId, imageFile)` — creates a new car, saves uploaded image to disk
- `updateCar(dto, imageFile)` — updates car fields and optionally replaces image
- `deleteCar(carId)` — deletes car; throws if active/pending rentals exist
- `toggleCarStatus(carId)` — toggles AVAILABLE ↔ IN_SERVICE; throws if car is RENTED
- `toUpdateDto(carId)` — loads car into `UpdateCarDto` for the update form
- `getAllBrands()`, `getAllCarModels()`, `getAllCategories()` — dropdown data for filters and forms
- Converts `Car` entities to `CarDto` internally via `toDto()`

### `CarSpec`
Static specification class — provides Spring Data `Specification<Car>` predicates for DB-level filtering:

| Method | Description |
|---|---|
| `ofCompany(company)` | Cars belonging to a given company |
| `statusAvailable()` | Cars with status AVAILABLE |
| `freeInPeriod(start, end)` | Cars not rented (non-CANCELLED rental) in the given date range |
| `withBrands(brands)` | Cars whose model brand is in the list |
| `withBodyTypes(bodyTypes)` | Cars whose body type is in the list |
| `withCategories(categories)` | Cars whose category name is in the list |
| `withTransmissions(transmissions)` | Cars whose transmission type is in the list |
| `withTractions(tractions)` | Cars whose traction type is in the list |
| `withMinYear(year)` | Cars whose model year ≥ year |
| `withMaxYear(year)` | Cars whose model year ≤ year |
| `fromFilter(filters)` | Combines all `CarFilterDto` fields into a single specification |

### `CustomUserDetailsService`
Spring Security integration — loads user by username for authentication.

---

## DTOs

| DTO | Direction | Description |
|---|---|---|
| `WebUserDTO` | Request | Registration form data |
| `UserDto` | Response | Flat user representation for views — no nested entity access |
| `CarRentalCompanyDto` | Request + Response | Company data for both forms and views |
| `CarDto` | Response | Flat car representation for views — all fields flattened from Car + CarModel + related entities |
| `CarFormDto` | Request | Add car form — carModelId, licencePlate, color, mileage |
| `UpdateCarDto` | Request + Response | Update car form — same fields plus status and display-only model info |
| `CarFilterDto` | Request | Filter parameters for car listing pages |

---

## Exceptions

| Exception | When thrown |
|---|---|
| `UserNotFoundException` | User ID or username does not exist |
| `CompanyNotFoundException` | Company ID does not exist |
| `InvalidRoleAssignmentException` | Assigning a role equal to or higher than the assigner's own role |
| `CompanyRequiredException` | Assigning `EMPLOYEE` or `MANAGER` role without selecting a company |

---

## Templates

```
templates/
├── auth/
│   ├── login.html
│   ├── registration-form.html
│   └── registration-confirmation.html
├── admin/
│   ├── staff.html                  # Manage all user accounts (SUPER_ADMIN)
│   ├── show-companies-table.html   # List companies
│   ├── show-company-form.html      # Add company
│   └── update-company-form.html    # Edit company
├── manager/
│   └── staff.html                  # Manage company employees (MANAGER)
├── cars/
│   ├── show-cars.html              # Staff view — all company cars, with filters and actions
│   ├── available-cars.html         # Client view — available cars for selected dates, with filters and Rent button
│   ├── add-car-form.html           # Add new car (EMPLOYEE/MANAGER)
│   ├── update-car-form.html        # Edit car (EMPLOYEE/MANAGER)
│   ├── add-car-model-form.html
│   ├── add-engine-form.html
│   ├── add-category-form.html
│   └── add-transmission-form.html
├── rentals/
│   ├── all-rentals.html
│   └── new-rental-form.html
├── profile/
│   └── edit-profile-form.html
└── home.html
```

---

## Database

Schema is managed by Flyway. The initial migration (`V1__create_tables.sql`) creates all tables.

Car images are stored on disk under the directory configured by `app.upload-dir` and served via `/uploads/**`.

Connection is configured via environment variables:

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3307/car_rental` |
| `SPRING_DATASOURCE_USERNAME` | `car_rental_user` |
| `SPRING_DATASOURCE_PASSWORD` | *(empty)* |
| `app.upload-dir` | *(required)* |
