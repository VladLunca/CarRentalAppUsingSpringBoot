package com.carreantalapp.app.services.car;

import com.carreantalapp.app.model.Car;
import com.carreantalapp.app.model.CarRentalCompany;
import com.carreantalapp.app.model.Rental;
import com.carreantalapp.app.model.utils.*;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import com.carreantalapp.app.dto.CarFilterDto;

import java.time.LocalDate;
import java.util.List;

public class CarSpec {

    public static Specification<Car> fromFilter(CarFilterDto filters) {
        if (filters == null) return (car, query, criteriaBuilder) -> null;
        return Specification.where(withBrands(filters.getBrands()))
                .and(withBodyTypes(filters.getBodyTypes()))
                .and(withCategories(filters.getCategories()))
                .and(withTransmissions(filters.getTransmissions()))
                .and(withTractions(filters.getTractions()))
                .and(withMinYear(filters.getFirstYear()))
                .and(withMaxYear(filters.getLastYear()));
    }


    public static Specification<Car> ofCompany(CarRentalCompany company) {
        return (car, query, criteriaBuilder) -> criteriaBuilder.equal(car.get("carRentalCompany"), company);
    }

    public static Specification<Car> statusAvailable() {
        return (car, query, criteriaBuilder) -> criteriaBuilder.equal(car.get("status"), CarStatus.AVAILABLE);
    }

    public static Specification<Car> freeInPeriod(LocalDate start, LocalDate end) {
        return (car, query, criteriaBuilder) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Rental> r = sub.from(Rental.class);
            sub.select(r.get("car").get("id"))
                    .where(criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(r.get("startDate"), end),
                            criteriaBuilder.greaterThanOrEqualTo(r.get("endDate"), start),
                            criteriaBuilder.notEqual(r.get("status"), RentalStatus.CANCELLED)
                    ));
            return criteriaBuilder.not(car.get("id").in(sub));
        };
    }

    public static Specification<Car> withBrands(List<String> brands) {
        return (car, query, criteriaBuilder) ->
                (brands == null || brands.isEmpty()) ? null : car.get("carModel").get("brand").in(brands);
    }

    public static Specification<Car> withBodyTypes(List<CarBodyTypes> bodyTypes) {
        return (car, query, criteriaBuilder) -> (bodyTypes == null || bodyTypes.isEmpty()) ? null : car.get("carModel").get("carBody").get("name").in(bodyTypes);
    }

    public static Specification<Car> withCategories(List<String> categories) {
        return (car, query, criteriaBuilder) -> (categories == null || categories.isEmpty()) ? null : car.get("carModel").get("category").get("categoryName").in(categories);
    }

    public static Specification<Car> withTransmissions(List<TransmissionTypes> transmissions) {
        return (car, query, criteriaBuilder) -> (transmissions == null || transmissions.isEmpty()) ? null : car.get("carModel").get("transmission").get("transmissionType").in(transmissions);
    }

    public static Specification<Car> withTractions(List<TractionTypes> tractions) {
        return (car, query, criteriaBuilder) -> (tractions == null || tractions.isEmpty()) ? null : car.get("carModel").get("traction").in(tractions);
    }

    public static Specification<Car> withMinYear(Integer minYear) {
        return (car, query, criteriaBuilder) -> minYear == null ? null : criteriaBuilder.greaterThanOrEqualTo(car.get("carModel").get("year"), minYear);
    }

    public static Specification<Car> withMaxYear(Integer maxYear) {
        return (car, query, criteriaBuilder) -> maxYear == null ? null : criteriaBuilder.lessThanOrEqualTo(car.get("carModel").get("year"), maxYear);
    }
}
