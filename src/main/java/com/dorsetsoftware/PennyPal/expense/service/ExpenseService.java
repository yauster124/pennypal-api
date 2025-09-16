package com.dorsetsoftware.PennyPal.expense.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.account.repository.AccountRepository;
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

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final Long INCOME_ID = 8L;

    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository,
            AccountRepository accountRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Page<ExpenseDto> getExpenses(
            String username,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        User user = userRepository.findByUsername(username);
        Page<Expense> expenses = expenseRepository.findByUserAndOptionalDateRange(
                user,
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

    public List<CategoryExpenseSummaryDto> getCategoryTotalsForUser(String username, LocalDate startDate, List<Long> accountIds) {
        User user = userRepository.findByUsername(username);
        LocalDate effectiveStart = (startDate != null) ? startDate : LocalDate.of(1970, 1, 1);

        return expenseRepository
                .findRootCategoryTotalsForUserSinceAndAccountIds(user, effectiveStart, accountIds);
    }

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

        Expense expense = new Expense(
                dto.getName(),
                amount,
                dto.getDate(),
                category,
                account,
                user);

        return ExpenseMapper.toDto(expenseRepository.save(expense));
    }

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

        expense.setName(dto.getName());
        expense.setAmount(amount);
        expense.setDate(dto.getDate());

        if (!expense.getCategory().getId().equals(dto.getCategoryId())) {
            expense.setCategory(category);
        }

        if (!expense.getAccount().getId().equals(dto.getAccountId())) {
            Account account = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            expense.setAccount(account);
        }

        Expense savedExpense = expenseRepository.save(expense);
        return ExpenseMapper.toDto(savedExpense);
    }

    public Boolean deleteExpense(Long expenseId) {
        if (expenseRepository.existsById(expenseId)) {
            expenseRepository.deleteById(expenseId);

            return true;
        }

        return false;
    }
}
