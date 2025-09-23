package com.dorsetsoftware.PennyPal.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferUpdateDto {
    private BigDecimal amount;
    private LocalDate date;
    private Long accountFromId;
    private Long accountToId;

    public TransferUpdateDto() {
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

    public void setAccountFromId(Long accountId) {
        this.accountFromId = accountId;
    }

    public Long getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(Long accountId) {
        this.accountToId = accountId;
    }
}
