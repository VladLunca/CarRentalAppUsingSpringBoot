package com.carreantalapp.app.services.car.components;

import com.carreantalapp.app.exceptions.CarBodyInUseException;
import com.carreantalapp.app.exceptions.CategoryInUseException;
import com.carreantalapp.app.exceptions.EngineInUseException;
import com.carreantalapp.app.exceptions.TransmissionInUseException;
import com.carreantalapp.app.repositories.CarBodyRepository;
import com.carreantalapp.app.repositories.CarModelRepository;
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
@DisplayName("Shared components cannot be deleted while a CarModel still references them")
class ComponentDeleteGuardsTest {

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private EngineRepository engineRepository;

    @Mock
    private TransmissionRepository transmissionRepository;

    @Mock
    private CarBodyRepository carBodyRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private EngineService engineService;

    @InjectMocks
    private TransmissionService transmissionService;

    @InjectMocks
    private CarBodyService carBodyService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void engineDeleteIsBlockedWhileReferenced() {
        when(carModelRepository.existsByEngineId(3L)).thenReturn(true);

        assertThatThrownBy(() -> engineService.deleteEngine(3L))
                .isInstanceOf(EngineInUseException.class);
        verify(engineRepository, never()).deleteById(anyLong());
    }

    @Test
    void engineDeleteProceedsWhenUnused() {
        when(carModelRepository.existsByEngineId(3L)).thenReturn(false);

        engineService.deleteEngine(3L);

        verify(engineRepository).deleteById(3L);
    }

    @Test
    void transmissionDeleteIsBlockedWhileReferenced() {
        when(carModelRepository.existsByTransmissionId(4L)).thenReturn(true);

        assertThatThrownBy(() -> transmissionService.deleteTransmission(4L))
                .isInstanceOf(TransmissionInUseException.class);
        verify(transmissionRepository, never()).deleteById(anyLong());
    }

    @Test
    void transmissionDeleteProceedsWhenUnused() {
        when(carModelRepository.existsByTransmissionId(4L)).thenReturn(false);

        transmissionService.deleteTransmission(4L);

        verify(transmissionRepository).deleteById(4L);
    }

    @Test
    void carBodyDeleteIsBlockedWhileReferenced() {
        when(carModelRepository.existsByCarBody_Id(5L)).thenReturn(true);

        assertThatThrownBy(() -> carBodyService.deleteCarBody(5L))
                .isInstanceOf(CarBodyInUseException.class);
        verify(carBodyRepository, never()).deleteById(anyLong());
    }

    @Test
    void carBodyDeleteProceedsWhenUnused() {
        when(carModelRepository.existsByCarBody_Id(5L)).thenReturn(false);

        carBodyService.deleteCarBody(5L);

        verify(carBodyRepository).deleteById(5L);
    }

    @Test
    void categoryDeleteIsBlockedWhileReferenced() {
        when(carModelRepository.existsByCategory_Id(6L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(6L))
                .isInstanceOf(CategoryInUseException.class);
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void categoryDeleteProceedsWhenUnused() {
        when(carModelRepository.existsByCategory_Id(6L)).thenReturn(false);

        categoryService.deleteCategory(6L);

        verify(categoryRepository).deleteById(6L);
    }
}
