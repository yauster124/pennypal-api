package com.dorsetsoftware.PennyPal.account.dto;

import java.math.BigDecimal;

public class AccountCreateDto {
    private String name;
    private BigDecimal balance;

    public AccountCreateDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
