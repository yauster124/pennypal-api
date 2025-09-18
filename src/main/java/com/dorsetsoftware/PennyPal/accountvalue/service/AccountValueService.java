package com.dorsetsoftware.PennyPal.accountvalue.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.account.repository.AccountRepository;
import com.dorsetsoftware.PennyPal.accountvalue.dto.AccountValuePoint;
import com.dorsetsoftware.PennyPal.accountvalue.entity.AccountValue;
import com.dorsetsoftware.PennyPal.accountvalue.repository.AccountValueRepository;
import com.dorsetsoftware.PennyPal.user.entity.User;
import com.dorsetsoftware.PennyPal.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AccountValueService {
    @Autowired
    private AccountValueRepository accountValueRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void snapshotAllAccounts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate today = LocalDate.now();

        List<Account> accounts = accountRepository.findByUser(user);

        for (Account account : accounts) {
            Optional<AccountValue> existing = accountValueRepository.findByAccountAndDate(account, today);
            if (existing.isPresent()) {
                existing.get().setValue(account.getBalance());
            } else {
                AccountValue av = new AccountValue();
                av.setAccount(account);
                av.setValue(account.getBalance());
                av.setDate(today);
                accountValueRepository.save(av);
            }
        }
    }

    public Map<String, List<AccountValuePoint>> getAccountValuesWithTotal(String username, LocalDate startDate) {
        User user = userRepository.findByUsername(username);
        LocalDate effectiveStart = (startDate != null) ? startDate : LocalDate.of(1970, 1, 1);
        List<AccountValue> values = accountValueRepository
                .findByAccountUserAndDateGreaterThanEqual(user, effectiveStart);

        // Group by account name
        Map<String, List<AccountValuePoint>> result = values.stream()
                .collect(Collectors.groupingBy(
                        av -> av.getAccount().getName(),
                        Collectors.mapping(
                                av -> new AccountValuePoint(av.getDate(), av.getValue()),
                                Collectors.toList())));

        // Build totals by date
        Map<LocalDate, BigDecimal> totalsByDate = values.stream()
                .collect(Collectors.groupingBy(
                        AccountValue::getDate,
                        Collectors.mapping(
                                AccountValue::getValue,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // Add totals to result map under special key "TOTAL"
        List<AccountValuePoint> totalSeries = totalsByDate.entrySet().stream()
                .map((Map.Entry<LocalDate, BigDecimal> e) -> new AccountValuePoint(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(AccountValuePoint::getDate))
                .collect(Collectors.toList());

        result.put("TOTAL", totalSeries);

        // Sort each account’s list by date too
        result.replaceAll((_, v) -> v.stream()
                .sorted(Comparator.comparing(AccountValuePoint::getDate))
                .collect(Collectors.toList()));

        return result;
    }
}
