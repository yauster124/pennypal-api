package com.dorsetsoftware.PennyPal.expense.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dorsetsoftware.PennyPal.category.dto.CategoryExpenseSummaryDto;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseCreateDto;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseDto;
import com.dorsetsoftware.PennyPal.expense.dto.ExpenseUpdateDto;
import com.dorsetsoftware.PennyPal.expense.dto.TransferCreateDto;
import com.dorsetsoftware.PennyPal.expense.service.ExpenseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public Page<ExpenseDto> getExpenses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        return expenseService.getExpenses(userDetails.getUsername(), searchQuery, categoryIds, startDate, endDate,
                pageable);
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> accountIds) {
        BigDecimal total = expenseService.getTotalAmount(userDetails.getUsername(), startDate, endDate, accountIds);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ExpenseDto>> getRecentExpenses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) List<Long> accountIds) {
        List<ExpenseDto> expenses = expenseService.getRecentExpensesForUser(userDetails.getUsername(), accountIds);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/category-totals")
    public ResponseEntity<List<CategoryExpenseSummaryDto>> getCategoryTotals(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) List<Long> accountIds) {
        List<CategoryExpenseSummaryDto> totals = expenseService
                .getCategoryTotalsForUser(userDetails.getUsername(), startDate, accountIds);

        return ResponseEntity.ok(totals);
    }

    @GetMapping("/account-values")
    public ResponseEntity<List<Map<String, Object>>> getAccountValues(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok(expenseService.getMultiAccountSeries(userDetails.getUsername(), startDate));
    }

    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ExpenseCreateDto dto) {
        return ResponseEntity.ok(expenseService.createExpense(dto, userDetails.getUsername()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ExpenseDto> createTransfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TransferCreateDto dto) {
        return ResponseEntity.ok(expenseService.createTransfer(dto, userDetails.getUsername()));
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseDto> updateExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long expenseId,
            @RequestBody ExpenseUpdateDto dto) {
        return ResponseEntity.ok(expenseService.updateExpense(expenseId, dto));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Boolean> deleteExpense(@PathVariable Long expenseId) {
        return ResponseEntity.ok(expenseService.deleteExpense(expenseId));
    }
}
