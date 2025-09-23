package com.dorsetsoftware.PennyPal.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dorsetsoftware.PennyPal.account.dto.AccountDto;

public class TransferDto {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private AccountDto accountFrom;
    private AccountDto accountTo;

    public TransferDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public AccountDto getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(AccountDto account) {
        this.accountFrom = account;
    }

    public AccountDto getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(AccountDto account) {
        this.accountTo = account;
    }
}
