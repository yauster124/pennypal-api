package com.dorsetsoftware.PennyPal.category.dto;

import java.math.BigDecimal;

public class CategoryExpenseSummaryDto {
    private Long categoryId;
    private String categoryName;
    private BigDecimal totalAmount;

    public CategoryExpenseSummaryDto(Long categoryId, String categoryName, BigDecimal totalAmount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
