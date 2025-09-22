package com.dorsetsoftware.PennyPal.accountvalue.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
    public void snapshotAllAccounts(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Account> accounts = accountRepository.findByUser(user);

        for (Account account : accounts) {
            Optional<AccountValue> existing = accountValueRepository.findByAccountAndDate(account, date);
            if (existing.isPresent()) {
                existing.get().setValue(account.getBalance());
            } else {
                AccountValue av = new AccountValue();
                av.setAccount(account);
                av.setValue(account.getBalance());
                av.setDate(date);
                accountValueRepository.save(av);
            }
        }
    }

    public Map<String, List<AccountValuePoint>> getAccountValuesWithTotal(String username, LocalDate startDate) {
        AccountValue earliestAccountValue = accountValueRepository.findFirstByAccountUserUsernameOrderByDateAsc(username);
        if (earliestAccountValue == null) {
            return new HashMap<>();
        }

        LocalDate earliestAccountValueDate = earliestAccountValue.getDate();
        LocalDate effectiveStart;
        if (startDate != null) {
            effectiveStart = earliestAccountValueDate.isBefore(startDate) ? earliestAccountValueDate : startDate;
        } else {
            effectiveStart = earliestAccountValueDate;
        }

        List<AccountValue> values = accountValueRepository
                .findByAccountUserUsernameAndDateGreaterThanEqualOrderByDateAsc(username, effectiveStart);

        Map<String, List<AccountValuePoint>> result = values.stream()
                .collect(Collectors.groupingBy(
                        av -> av.getAccount().getName(),
                        Collectors.mapping(
                                av -> new AccountValuePoint(av.getDate(), av.getValue()),
                                Collectors.toList())));

        Map<String, List<AccountValuePoint>> accountValueMap = new HashMap<>();
        for (Map.Entry<String, List<AccountValuePoint>> entry : result.entrySet()) {
            String account = entry.getKey();
            List<AccountValuePoint> rawAccountValues = entry.getValue();
            List<AccountValuePoint> newAccountValues = new ArrayList<>();

            for (int i = 0; i < rawAccountValues.size(); i++) {
                AccountValuePoint accountValuePoint = rawAccountValues.get(i);
                BigDecimal value = accountValuePoint.getValue();

                LocalDate rangeStartDate = accountValuePoint.getDate();
                LocalDate rangeEndDate;
                if (i < rawAccountValues.size() - 1) {
                    rangeEndDate = rawAccountValues.get(i + 1).getDate();
                } else {
                    rangeEndDate = LocalDate.now().plusDays(1);
                }

                for (LocalDate date : rangeStartDate.datesUntil(rangeEndDate).toList()) {
                    newAccountValues.add(new AccountValuePoint(date, value));
                }
            }

            accountValueMap.put(account, newAccountValues);
        }

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

        List<AccountValuePoint> totalsList = new ArrayList<>();
        for (int i = 0; i < totalSeries.size(); i++) {
            AccountValuePoint accountValuePoint = totalSeries.get(i);
            BigDecimal value = accountValuePoint.getValue();

            LocalDate rangeStartDate = accountValuePoint.getDate();
            LocalDate rangeEndDate;
            if (i < totalSeries.size() - 1) {
                rangeEndDate = totalSeries.get(i + 1).getDate();
            } else {
                rangeEndDate = LocalDate.now().plusDays(1);
            }

            for (LocalDate date : rangeStartDate.datesUntil(rangeEndDate).toList()) {
                totalsList.add(new AccountValuePoint(date, value));
            }
        }

        accountValueMap.put("TOTAL", totalsList);

        return accountValueMap;
    }
}
