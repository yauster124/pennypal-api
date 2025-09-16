package com.dorsetsoftware.PennyPal.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dorsetsoftware.PennyPal.category.dto.CategoryDto;
import com.dorsetsoftware.PennyPal.category.mapper.CategoryMapper;
import com.dorsetsoftware.PennyPal.category.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }
}
