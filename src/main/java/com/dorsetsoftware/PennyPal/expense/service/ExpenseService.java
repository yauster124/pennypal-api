package com.dorsetsoftware.PennyPal.expense.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dorsetsoftware.PennyPal.account.dto.AccountDto;
import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.account.repository.AccountRepository;
import com.dorsetsoftware.PennyPal.account.service.AccountService;
import com.dorsetsoftware.PennyPal.category.dto.CategoryExpenseSummaryDto;
import com.dorsetsoftware.PennyPal.category.entity.Category;
import com.dorsetsoftware.PennyPal.category.model.CategoryType;
import com.dorsetsoftware.PennyPal.category.repository.CategoryRepository;
import com.dorsetsoftware.PennyPal.expense.dto.AccountBalanceDto;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseCreateDto;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseDto;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseUpdateDto;
import com.dorsetsoftware.PennyPal.expense.dto.TransferCreateDto;
import com.dorsetsoftware.PennyPal.expense.entity.Expense;
import com.dorsetsoftware.PennyPal.expense.mapper.ExpenseMapper;
import com.dorsetsoftware.PennyPal.expense.model.ExpenseType;
import com.dorsetsoftware.PennyPal.expense.repository.ExpenseRepository;
import com.dorsetsoftware.PennyPal.user.entity.User;
import com.dorsetsoftware.PennyPal.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountService accountService;

    private final Long CATEGORY_TRANSFER_IN_ID = 53L;
    private final Long CATEGORY_TRANSFER_OUT_ID = 54L;

    public Page<ExpenseDto> getExpenses(
            String username,
            String searchQuery,
            List<Long> categoryIds,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        User user = userRepository.findByUsername(username);
        String safeSearchQuery = (searchQuery == null || searchQuery.isBlank())
                ? null
                : "%" + searchQuery.toLowerCase() + "%";
        Page<Expense> expenses = expenseRepository.findByUserAndOptionalDateRange(
                user,
                safeSearchQuery,
                categoryIds,
                startDate,
                endDate,
                pageable);

        return expenses.map(ExpenseMapper::toDto);
    }

    public BigDecimal getTotalAmount(
            String username,
            LocalDate startDate,
            LocalDate endDate,
            List<Long> accountIds) {
        User user = userRepository.findByUsername(username);
        LocalDate effectiveStart = (startDate != null) ? startDate : LocalDate.of(1970, 1, 1);
        LocalDate effectiveEnd = (endDate != null) ? endDate : LocalDate.of(9999, 12, 31);

        return expenseRepository.getTotalAmountForUserAndAccounts(user, effectiveStart, effectiveEnd, accountIds);
    }

    public List<ExpenseDto> getRecentExpensesForUser(String username, List<Long> accountIds) {
        User user = userRepository.findByUsername(username);
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Expense> expenses = expenseRepository
                .findTop5ByUserAndAccountIdInAndDateAfterOrderByDateDesc(user, accountIds, thirtyDaysAgo);

        return expenses.stream()
                .map(ExpenseMapper::toDto)
                .toList();
    }

    public List<CategoryExpenseSummaryDto> getCategoryTotalsForUser(String username, LocalDate startDate,
            List<Long> accountIds) {
        User user = userRepository.findByUsername(username);
        LocalDate effectiveStart = (startDate != null) ? startDate : LocalDate.of(1970, 1, 1);

        return expenseRepository
                .findRootCategoryTotalsForUserSinceAndAccountIds(user, effectiveStart, accountIds,
                        CategoryType.EXPENSE);
    }

    @Transactional
    public ExpenseDto createTransfer(TransferCreateDto dto, String username) {
        User user = userRepository.findByUsername(username);
        Account accountFrom = accountRepository.findById(dto.getAccountFromId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Account accountTo = accountRepository.findById(dto.getAccountToId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Expense transferFrom = new Expense();
        Category transferOut = categoryRepository.findById(CATEGORY_TRANSFER_OUT_ID)
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));;
        transferFrom.setName(String.format("%s to %s", accountFrom.getName(), accountTo.getName()));
        transferFrom.setAmount(dto.getAmount().negate());
        transferFrom.setDate(dto.getDate());
        transferFrom.setAccount(accountFrom);
        transferFrom.setUser(user);
        transferFrom.setCategory(transferOut);
        transferFrom.setType(ExpenseType.TRANSFER);
        expenseRepository.save(transferFrom);

        Expense transferTo = new Expense();
        Category transferIn = categoryRepository.findById(CATEGORY_TRANSFER_IN_ID)
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));;
        transferTo.setName(String.format("%s to %s", accountFrom.getName(), accountTo.getName()));
        transferTo.setAmount(dto.getAmount());
        transferTo.setDate(dto.getDate());
        transferTo.setAccount(accountTo);
        transferTo.setUser(user);
        transferTo.setCategory(transferIn);
        transferTo.setType(ExpenseType.TRANSFER);
        Expense saved = expenseRepository.save(transferTo);

        return ExpenseMapper.toDto(saved);
    }

    @Transactional
    public ExpenseDto createExpense(ExpenseCreateDto dto, String username) {
        User user = userRepository.findByUsername(username);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal amount;
        ExpenseType type;
        if (category.getCategoryType() == CategoryType.INCOME
                || (category.getParent() != null && category.getParent().getCategoryType() == CategoryType.INCOME)) {
            amount = dto.getAmount().abs();
            type = ExpenseType.INCOME;
        } else {
            amount = dto.getAmount().abs().negate();
            type = ExpenseType.EXPENSE;
        }

        Expense expense = new Expense(
                dto.getName(),
                amount,
                dto.getDate(),
                category,
                account,
                type,
                user);

        return ExpenseMapper.toDto(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseDto updateExpense(Long expenseId, ExpenseUpdateDto dto) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        BigDecimal amount;
        if (category.getCategoryType() == CategoryType.INCOME
                || (category.getParent() != null && category.getParent().getCategoryType() == CategoryType.INCOME)) {
            amount = dto.getAmount().abs();
        } else {
            amount = dto.getAmount().abs().negate();
        }

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        expense.setName(dto.getName());
        expense.setAmount(amount);
        expense.setDate(dto.getDate());

        if (!expense.getCategory().getId().equals(dto.getCategoryId())) {
            expense.setCategory(category);
        }

        if (!expense.getAccount().getId().equals(dto.getAccountId())) {
            expense.setAccount(account);
        }

        Expense savedExpense = expenseRepository.save(expense);
        return ExpenseMapper.toDto(savedExpense);
    }

    @Transactional
    public Boolean deleteExpense(Long expenseId) {
        expenseRepository.deleteById(expenseId);

        return true;
    }

    public List<Map<String, Object>> getMultiAccountSeries(String username, LocalDate start) {
        // If no start provided, use earliest expense for the user
        LocalDate effectiveStart = start;
        if (effectiveStart == null) {
            effectiveStart = expenseRepository.findEarliestDateForUser(username);
            if (effectiveStart == null) {
                // no expenses at all -> return empty list
                return Collections.emptyList();
            }
        }

        List<AccountDto> accounts = accountService.getAccountsForUser(username);
        Map<String, Map<LocalDate, BigDecimal>> accountSeries = new HashMap<>();

        for (AccountDto account : accounts) {
            List<AccountBalanceDto> series = getBalanceTimeSeries(account.getId(), effectiveStart);
            Map<LocalDate, BigDecimal> byDate = series.stream()
                    .collect(Collectors.toMap(AccountBalanceDto::getDate, AccountBalanceDto::getBalance));
            accountSeries.put(account.getName(), byDate);
        }

        // Step 2: Build merged list
        List<Map<String, Object>> merged = new ArrayList<>();
        LocalDate current = effectiveStart;
        while (!current.isAfter(LocalDate.now())) {
            Map<String, Object> row = new HashMap<>();
            row.put("date", current);

            BigDecimal runningTotal = BigDecimal.ZERO;
            for (String accountName : accountSeries.keySet()) {
                BigDecimal amount = accountSeries.get(accountName).get(current);
                runningTotal = runningTotal.add(amount);
                row.put(accountName, amount);
            }
            row.put("Total", runningTotal);

            merged.add(row);
            current = current.plusDays(1);
        }

        return merged;
    }

    public List<AccountBalanceDto> getBalanceTimeSeries(Long accountId, LocalDate startDate) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + accountId));

        LocalDate effectiveStart = startDate;
        if (effectiveStart == null) {
            effectiveStart = expenseRepository.findEarliestDateForAccount(accountId);
            if (effectiveStart == null) {
                // no expenses at all -> just return a single point with initial balance
                return List.of(new AccountBalanceDto(LocalDate.now(), account.getInitialBalance()));
            }
        }

        LocalDate endDate = LocalDate.now();

        // get sum of transactions before effectiveStart
        BigDecimal sumBefore = expenseRepository.sumAmountBeforeDate(accountId, effectiveStart);
        if (sumBefore == null)
            sumBefore = BigDecimal.ZERO;

        BigDecimal runningBalance = account.getInitialBalance().add(sumBefore);

        // fetch transactions between effectiveStart and today
        List<Expense> txs = expenseRepository
                .findByAccountIdAndOptionalDates(accountId, effectiveStart, endDate);

        // build dense series
        List<AccountBalanceDto> series = new ArrayList<>();
        LocalDate currentDate = effectiveStart;
        int idx = 0;

        while (!currentDate.isAfter(endDate)) {
            // apply txs for this day
            while (idx < txs.size() && !txs.get(idx).getDate().isAfter(currentDate)) {
                runningBalance = runningBalance.add(txs.get(idx).getAmount());
                idx++;
            }

            series.add(new AccountBalanceDto(currentDate, runningBalance));
            currentDate = currentDate.plusDays(1);
        }

        return series;
    }

}
