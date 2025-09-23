package com.dorsetsoftware.PennyPal.transfer.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_from_id")
    private Account accountFrom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_to_id")
    private Account accountTo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    protected Transfer() {}

    public Transfer(BigDecimal amount, LocalDate date, Account accountFrom, Account accountTo, User user) {
        this.amount = amount;
        this.date = date;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return String.format("%s → %s", accountFrom.getName(), accountTo.getName());
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

    public Account getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Account account) {
        this.accountFrom = account;
    }

    public Account getAccountTo() {
        return accountFrom;
    }

    public void setAccountTo(Account account) {
        this.accountFrom = account;
    }

    public User getUser() {
        return user;
    }
}
