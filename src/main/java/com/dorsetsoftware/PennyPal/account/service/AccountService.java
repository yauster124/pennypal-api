package com.dorsetsoftware.PennyPal.account.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dorsetsoftware.PennyPal.account.dto.AccountCreateDto;
import com.dorsetsoftware.PennyPal.account.dto.AccountDto;
import com.dorsetsoftware.PennyPal.account.dto.AccountSummaryDto;
import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.account.mapper.AccountMapper;
import com.dorsetsoftware.PennyPal.account.repository.AccountRepository;
import com.dorsetsoftware.PennyPal.user.entity.User;
import com.dorsetsoftware.PennyPal.user.repository.UserRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<AccountDto> getAccountsForUser(String username) {
        User user = userRepository.findByUsername(username);
        List<Account> accounts = accountRepository.findByUser(user);

        return accounts.stream()
                .map(account -> {
                    BigDecimal balance = getBalanceByAccount(account);
                    return AccountMapper.toDto(account, balance);
                })
                .toList();
    }

    public AccountSummaryDto createAccount(AccountCreateDto dto, String username) {
        User user = userRepository.findByUsername(username);
        Account account = new Account(
                dto.getName(),
                dto.getBalance(),
                user);

        return AccountMapper.toSummaryDto(accountRepository.save(account));
    }

    public BigDecimal getBalanceByAccount(Account account) {
        BigDecimal txSum = accountRepository.sumByAccountId(account.getId());

        return account.getInitialBalance().add(txSum != null ? txSum : BigDecimal.ZERO);
    }
}
