package com.dorsetsoftware.PennyPal.category.mapper;

import com.dorsetsoftware.PennyPal.category.dto.CategoryDto;
import com.dorsetsoftware.PennyPal.category.entity.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCategoryType(category.getCategoryType());
        
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }

        return dto;
    }
}
