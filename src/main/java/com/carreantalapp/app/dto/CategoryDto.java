package com.carreantalapp.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank
    private String categoryName;

    @NotBlank
    private String categoryDescription;
}
