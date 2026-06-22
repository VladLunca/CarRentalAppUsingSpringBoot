package com.carreantalapp.app.repositories;

import com.carreantalapp.app.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {

    @Query("select distinct cm.brand from CarModel cm order by cm.brand")
    List<String> findDistinctBrands();

    boolean existsByEngineId(Long engineId);

    boolean existsByTransmissionId(Long transmissionId);

    boolean existsByCategory_Id(Long categoryId);

    boolean existsByCarBody_Id(Long carBodyId);
}
