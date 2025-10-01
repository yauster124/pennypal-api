package com.dorsetsoftware.PennyPal.budget.dto;

import java.math.BigDecimal;

import com.dorsetsoftware.PennyPal.budget.model.BudgetType;
import com.dorsetsoftware.PennyPal.category.dto.CategoryDto;

public class BudgetDto {
    private Long id;
    private BigDecimal amount;
    private BigDecimal spent;
    private BudgetType type;
    private CategoryDto category;

    public BudgetDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }

    public BudgetType getType() {
        return type;
    }

    public void setType(BudgetType type) {
        this.type = type;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }
}
