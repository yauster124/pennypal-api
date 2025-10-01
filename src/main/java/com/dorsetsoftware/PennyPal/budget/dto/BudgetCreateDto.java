package com.dorsetsoftware.PennyPal.budget.dto;

import java.math.BigDecimal;

import com.dorsetsoftware.PennyPal.budget.model.BudgetType;

public class BudgetCreateDto {
    private BigDecimal amount;
    private BudgetType type;
    private Long categoryId;

    public BudgetCreateDto() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BudgetType getType() {
        return type;
    }

    public void setType(BudgetType type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategory(Long categoryId) {
        this.categoryId = categoryId;
    }
}
