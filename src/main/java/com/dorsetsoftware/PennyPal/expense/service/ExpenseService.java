package com.dorsetsoftware.PennyPal.expense.service;

import java.time.LocalDate;
import java.util.List;

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

    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository,
            AccountRepository accountRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
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

    public List<CategoryExpenseSummaryDto> getCategoryTotalsForUserLast30Days(String username, List<Long> accountIds) {
        User user = userRepository.findByUsername(username);
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        return expenseRepository
                .findCategoryTotalsForUserSinceAndAccountIds(user, thirtyDaysAgo, accountIds);
    }

    public ExpenseDto createExpense(ExpenseCreateDto dto, String username) {
        User user = userRepository.findByUsername(username);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Expense expense = new Expense(
                dto.getName(),
                dto.getAmount(),
                dto.getDate(),
                category,
                account,
                user);

        return ExpenseMapper.toDto(expenseRepository.save(expense));
    }

    public ExpenseDto updateExpense(Long expenseId, ExpenseUpdateDto dto) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setName(dto.getName());
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());

        if (!expense.getCategory().getId().equals(dto.getCategoryId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
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
