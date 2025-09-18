package com.dorsetsoftware.PennyPal.accountvalue.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountValuePoint {
    private LocalDate date;
    private BigDecimal value;

    public AccountValuePoint(LocalDate date, BigDecimal value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getValue() {
        return value;
    }
}
