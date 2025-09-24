package com.dorsetsoftware.PennyPal.category.dto;

import com.dorsetsoftware.PennyPal.category.model.CategoryType;

public class CategoryDto {
    private Long id;
    private String name;
    private CategoryType type;
    private Long parentId;

    public CategoryDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setCategoryType(CategoryType type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
