package com.dorsetsoftware.PennyPal.expense.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.account.repository.AccountRepository;
import com.dorsetsoftware.PennyPal.accountvalue.service.AccountValueService;
import com.dorsetsoftware.PennyPal.category.dto.CategoryExpenseSummaryDto;
import com.dorsetsoftware.PennyPal.category.entity.Category;
import com.dorsetsoftware.PennyPal.category.repository.CategoryRepository;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseCreateDto;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseDto;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseUpdateDto;
import com.dorsetsoftware.PennyPal.expense.entity.Expense;
import com.dorsetsoftware.PennyPal.expense.mapper.ExpenseMapper;
import com.dorsetsoftware.PennyPal.expense.repository.ExpenseRepository;
import com.dorsetsoftware.PennyPal.user.entity.User;
import com.dorsetsoftware.PennyPal.user.repository.UserRepository;

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
    private AccountValueService accountValueService;
    private final Long INCOME_ID = 8L;

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
                .findRootCategoryTotalsForUserSinceAndAccountIds(user, effectiveStart, accountIds);
    }

    @Transactional
    public ExpenseDto createExpense(ExpenseCreateDto dto, String username) {
        User user = userRepository.findByUsername(username);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal amount;
        if (category.getId() == INCOME_ID
                || (category.getParent() != null && category.getParent().getId() == INCOME_ID)) {
            amount = dto.getAmount().abs();
        } else {
            amount = dto.getAmount().abs().negate();
        }

        account.updateBalance(amount);
        accountRepository.save(account);
        accountValueService.snapshotAllAccounts(user.getId(), dto.getDate());

        Expense expense = new Expense(
                dto.getName(),
                amount,
                dto.getDate(),
                category,
                account,
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
        if (category.getId() == INCOME_ID
                || (category.getParent() != null && category.getParent().getId() == INCOME_ID)) {
            amount = dto.getAmount().abs();
        } else {
            amount = dto.getAmount().abs().negate();
        }

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!expense.getAccount().getId().equals(dto.getAccountId())) {
            Account oldAccount = accountRepository.findById(expense.getAccount().getId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            oldAccount.updateBalance(expense.getAmount().negate());
            accountRepository.save(oldAccount);
            account.updateBalance(amount);
            accountRepository.save(account);
        } else {
            BigDecimal amountDifference = amount.subtract(expense.getAmount()).setScale(2, RoundingMode.HALF_UP);
            account.updateBalance(amountDifference);
            accountRepository.save(account);
        }
        accountValueService.snapshotAllAccounts(expense.getUser().getId(), dto.getDate());

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
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        Account account = expense.getAccount();
        account.updateBalance(expense.getAmount().negate());
        accountValueService.updateAllSnapshots(expense.getUser().getId(), expense.getDate(), expense.getAmount().negate());

        expenseRepository.deleteById(expenseId);

        return true;
    }
}
