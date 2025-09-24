package com.dorsetsoftware.PennyPal.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dorsetsoftware.PennyPal.account.dto.AccountSummaryDto;
import com.dorsetsoftware.PennyPal.category.dto.CategoryDto;

public class ExpenseDto {
    private Long id;
    private String name;
    private BigDecimal amount;
    private LocalDate date;
    private CategoryDto category;
    private AccountSummaryDto account;

    public ExpenseDto() {}

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public AccountSummaryDto getAccount() {
        return account;
    }

    public void setAccount(AccountSummaryDto account) {
        this.account = account;
    }
}
