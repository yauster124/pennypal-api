package com.dorsetsoftware.PennyPal.budget.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dorsetsoftware.PennyPal.budget.entity.Budget;
import com.dorsetsoftware.PennyPal.budget.model.BudgetType;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserUsernameAndTypeOrderByCategoryIdAsc(String username, BudgetType type);
}
