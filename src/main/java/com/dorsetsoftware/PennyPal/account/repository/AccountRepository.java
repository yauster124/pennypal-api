package com.dorsetsoftware.PennyPal.account.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.user.entity.User;
import com.dorsetsoftware.PennyPal.expense.entity.Expense;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.account.id = :accountId")
    BigDecimal sumByAccountId(@Param("accountId") Long accountId);
}
