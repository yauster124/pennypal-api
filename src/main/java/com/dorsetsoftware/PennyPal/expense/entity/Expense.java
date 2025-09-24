package com.dorsetsoftware.PennyPal.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.category.entity.Category;
import com.dorsetsoftware.PennyPal.expense.model.ExpenseType;
import com.dorsetsoftware.PennyPal.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public Expense() {}

    public Expense(String name, BigDecimal amount, LocalDate date, Category category, Account account, ExpenseType type, User user) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.account = account;
        this.type = type;
        this.user = user;
    }

    public Long getId() {
        return id;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
