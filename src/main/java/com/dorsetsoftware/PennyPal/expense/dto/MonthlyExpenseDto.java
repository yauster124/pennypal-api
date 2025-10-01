package com.dorsetsoftware.PennyPal.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MonthlyExpenseDto {
    private LocalDate month;
    private BigDecimal total;

    public MonthlyExpenseDto() {
    }

    public MonthlyExpenseDto(LocalDate month, BigDecimal total) {
        this.month = month;
        this.total = total;
    }

    public LocalDate getMonth() {
        return month;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
