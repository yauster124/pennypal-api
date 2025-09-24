package com.dorsetsoftware.PennyPal.account.entity;

import java.math.BigDecimal;

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
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal initialBalance;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public Account() {
    }

    public Account(String name, BigDecimal initialBalance, User user) {
        this.name = name;
        this.initialBalance = initialBalance;
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

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public User getUser() {
        return user;
    }
}
