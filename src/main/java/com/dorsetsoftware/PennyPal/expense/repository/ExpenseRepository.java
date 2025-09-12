package com.dorsetsoftware.PennyPal.expense.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dorsetsoftware.PennyPal.category.dto.CategoryExpenseSummaryDto;
import com.dorsetsoftware.PennyPal.expense.entity.Expense;
import com.dorsetsoftware.PennyPal.user.entity.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findTop5ByUserAndAccountIdInAndDateAfterOrderByDateDesc(
            User user,
            List<Long> accountIds,
            LocalDate startDate);

    @Query("""
                SELECT new com.dorsetsoftware.PennyPal.category.dto.CategoryExpenseSummaryDto(
                    e.category.id,
                    e.category.name,
                    SUM(e.amount)
                )
                FROM Expense e
                WHERE e.user = :user
                  AND e.date >= :startDate
                  AND e.account.id IN :accountIds
                GROUP BY e.category.id, e.category.name
                HAVING SUM(e.amount) > 0
            """)
    List<CategoryExpenseSummaryDto> findCategoryTotalsForUserSinceAndAccountIds(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("accountIds") List<Long> accountIds);
}
