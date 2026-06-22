package com.carreantalapp.app.services.car.components;

import com.carreantalapp.app.dto.CategoryDto;
import com.carreantalapp.app.exceptions.CategoryInUseException;
import com.carreantalapp.app.model.Category;
import com.carreantalapp.app.repositories.CarModelRepository;
import com.carreantalapp.app.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CarModelRepository carModelRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CarModelRepository carModelRepository) {
        this.categoryRepository = categoryRepository;
        this.carModelRepository = carModelRepository;
    }

    private CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setCategoryDescription(category.getCategoryDescription());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void saveCategory(CategoryDto dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setCategoryDescription(dto.getCategoryDescription());
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        if (carModelRepository.existsByCategory_Id(categoryId)) {
            throw new CategoryInUseException(categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }
}
