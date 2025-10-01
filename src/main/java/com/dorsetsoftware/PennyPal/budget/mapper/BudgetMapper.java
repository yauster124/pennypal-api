package com.dorsetsoftware.PennyPal.budget.mapper;

import com.dorsetsoftware.PennyPal.budget.dto.BudgetDto;
import com.dorsetsoftware.PennyPal.budget.entity.Budget;
import com.dorsetsoftware.PennyPal.category.mapper.CategoryMapper;

public class BudgetMapper {
    public static BudgetDto toDto(Budget budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        dto.setAmount(budget.getAmount());
        dto.setType(budget.getType());

        if (budget.getCategory() != null) {
            dto.setCategory(CategoryMapper.toDto(budget.getCategory()));
        }

        return dto;
    }
}
