package com.dorsetsoftware.PennyPal.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.user.entity.User;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
