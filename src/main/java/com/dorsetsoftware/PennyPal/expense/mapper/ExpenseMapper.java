package com.dorsetsoftware.PennyPal.expense.mapper;

import com.dorsetsoftware.PennyPal.account.mapper.AccountMapper;
import com.dorsetsoftware.PennyPal.category.mapper.CategoryMapper;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseDto;
import com.dorsetsoftware.PennyPal.expense.entity.Expense;

public class ExpenseMapper {
    public static ExpenseDto toDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setName(expense.getName());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());

        if (expense.getCategory() != null) {
            dto.setCategory(CategoryMapper.toDto(expense.getCategory()));
        }
        dto.setAccount(AccountMapper.toSummaryDto(expense.getAccount()));

        return dto;
    }
}
