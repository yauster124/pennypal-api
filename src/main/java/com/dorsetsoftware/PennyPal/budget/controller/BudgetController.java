package com.dorsetsoftware.PennyPal.budget.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dorsetsoftware.PennyPal.budget.dto.BudgetCreateDto;
import com.dorsetsoftware.PennyPal.budget.dto.BudgetDto;
import com.dorsetsoftware.PennyPal.budget.dto.BudgetUpdateDto;
import com.dorsetsoftware.PennyPal.budget.model.BudgetType;
import com.dorsetsoftware.PennyPal.budget.service.BudgetService;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetDto>> getBudgets(
         @AuthenticationPrincipal UserDetails userDetails,
         @RequestParam(required = false) BudgetType budgetType
    ) {
        return ResponseEntity.ok(budgetService.getBudgets(userDetails.getUsername(), budgetType));
    }

    @PostMapping
    public ResponseEntity<BudgetDto> createBudget(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody BudgetCreateDto dto
    ) {
        return ResponseEntity.ok(budgetService.createBudget(userDetails.getUsername(), dto));
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetDto> updateBudget(
        @PathVariable Long budgetId,
        @RequestBody BudgetUpdateDto dto
    ) {
        return ResponseEntity.ok(budgetService.updateBudget(budgetId, dto));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Boolean> deleteBudget(@PathVariable Long budgetId) {
        return ResponseEntity.ok(budgetService.deleteBudget(budgetId));
    }
}
