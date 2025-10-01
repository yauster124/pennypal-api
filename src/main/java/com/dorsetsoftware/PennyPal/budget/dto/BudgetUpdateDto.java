package com.dorsetsoftware.PennyPal.budget.dto;

import java.math.BigDecimal;

public class BudgetUpdateDto {
    private BigDecimal amount;

    public BudgetUpdateDto() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
