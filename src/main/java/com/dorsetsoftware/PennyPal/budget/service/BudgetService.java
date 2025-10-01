package com.dorsetsoftware.PennyPal.budget.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dorsetsoftware.PennyPal.budget.dto.BudgetCreateDto;
import com.dorsetsoftware.PennyPal.budget.dto.BudgetDto;
import com.dorsetsoftware.PennyPal.budget.dto.BudgetUpdateDto;
import com.dorsetsoftware.PennyPal.budget.entity.Budget;
import com.dorsetsoftware.PennyPal.budget.mapper.BudgetMapper;
import com.dorsetsoftware.PennyPal.budget.model.BudgetType;
import com.dorsetsoftware.PennyPal.budget.repository.BudgetRepository;
import com.dorsetsoftware.PennyPal.category.entity.Category;
import com.dorsetsoftware.PennyPal.category.repository.CategoryRepository;
import com.dorsetsoftware.PennyPal.expense.repository.ExpenseRepository;
import com.dorsetsoftware.PennyPal.user.entity.User;
import com.dorsetsoftware.PennyPal.user.repository.UserRepository;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;

    public List<BudgetDto> getBudgets(String username, BudgetType type) {
        return budgetRepository.findByUserUsernameAndTypeOrderByCategoryIdAsc(username, type)
                .stream()
                .map(budget -> {
                    Long categoryId = null;
                    if (budget.getCategory() != null) {
                        categoryId = budget.getCategory().getId();
                    }
                    
                    LocalDate startDate = LocalDate.now();
                    if (budget.getType() == BudgetType.MONTHLY) {
                        startDate = startDate.withDayOfMonth(1);
                    } else {
                        startDate = startDate.withDayOfYear(1);
                    }

                    BigDecimal spent = expenseRepository.sumByCategoryIdAndUser(categoryId, username, startDate);
                    BudgetDto dto = BudgetMapper.toDto(budget);
                    dto.setSpent(spent.abs());

                    return dto;
                })
                .toList();
    }

    public BudgetDto createBudget(String username, BudgetCreateDto dto) {
        Budget budget = new Budget();
        budget.setAmount(dto.getAmount());
        budget.setType(dto.getType());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            budget.setCategory(category);
        }

        User user = userRepository.findByUsername(username);
        budget.setUser(user);
        Budget saved = budgetRepository.save(budget);

        return BudgetMapper.toDto(saved);
    }

    @Transactional
    public BudgetDto updateBudget(Long budgetId, BudgetUpdateDto dto) {
        Budget budget = budgetRepository.findById(budgetId)
                    .orElseThrow(() -> new RuntimeException("Budget not found"));
        budget.setAmount(dto.getAmount());

        return BudgetMapper.toDto(budget);
    }

    @Transactional
    public Boolean deleteBudget(Long budgetId) {
        budgetRepository.deleteById(budgetId);

        return true;
    }
}
