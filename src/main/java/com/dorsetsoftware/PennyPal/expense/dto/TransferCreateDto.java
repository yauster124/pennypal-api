package com.dorsetsoftware.PennyPal.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferCreateDto {
    private BigDecimal amount;
    private LocalDate date;
    private Long accountFromId;
    private Long accountToId;

    public TransferCreateDto(){
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

    public Long getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(Long accountFromId) {
        this.accountFromId = accountFromId;
    }

    public Long getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(Long accountToId) {
        this.accountToId = accountToId;
    }
}
