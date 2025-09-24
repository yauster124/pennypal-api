package com.dorsetsoftware.PennyPal.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountBalanceDto {
    private LocalDate date;
    private BigDecimal balance;

    public AccountBalanceDto(LocalDate date, BigDecimal balance) {
        this.date = date;
        this.balance = balance;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
