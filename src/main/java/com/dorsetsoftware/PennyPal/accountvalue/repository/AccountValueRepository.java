package com.dorsetsoftware.PennyPal.accountvalue.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.accountvalue.entity.AccountValue;
import com.dorsetsoftware.PennyPal.user.entity.User;

@Repository
public interface AccountValueRepository extends JpaRepository<AccountValue, Long> {
    Optional<AccountValue> findByAccountAndDate(Account account, LocalDate date);

    List<AccountValue> findByAccountUserUsernameAndDateGreaterThanEqualOrderByDateAsc(String username, LocalDate date);

    AccountValue findFirstByAccountUserUsernameOrderByDateAsc(String username);
}
