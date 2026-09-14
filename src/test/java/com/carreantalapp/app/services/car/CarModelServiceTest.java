package com.carreantalapp.app.services.car;

import com.carreantalapp.app.exceptions.CarModelInUseException;
import com.carreantalapp.app.repositories.CarBodyRepository;
import com.carreantalapp.app.repositories.CarModelRepository;
import com.carreantalapp.app.repositories.CarRepository;
import com.carreantalapp.app.repositories.CategoryRepository;
import com.carreantalapp.app.repositories.EngineRepository;
import com.carreantalapp.app.repositories.TransmissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarModelService.deleteCarModel")
class CarModelServiceTest {

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private EngineRepository engineRepository;

    @Mock
    private TransmissionRepository transmissionRepository;

    @Mock
    private CarBodyRepository carBodyRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CarModelService carModelService;

    @Test
    void blockedWhileACarStillUsesTheModel() {
        when(carRepository.existsByCarModelId(7L)).thenReturn(true);

        assertThatThrownBy(() -> carModelService.deleteCarModel(7L))
                .isInstanceOf(CarModelInUseException.class);
        verify(carModelRepository, never()).deleteById(anyLong());
    }

    @Test
    void proceedsWhenNoCarUsesTheModel() {
        when(carRepository.existsByCarModelId(7L)).thenReturn(false);

        carModelService.deleteCarModel(7L);

        verify(carModelRepository).deleteById(7L);
    }
}
