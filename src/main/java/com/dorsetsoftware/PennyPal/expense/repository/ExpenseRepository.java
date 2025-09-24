package com.dorsetsoftware.PennyPal.expense.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dorsetsoftware.PennyPal.category.dto.CategoryExpenseSummaryDto;
import com.dorsetsoftware.PennyPal.category.model.CategoryType;
import com.dorsetsoftware.PennyPal.expense.entity.Expense;
import com.dorsetsoftware.PennyPal.user.entity.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("""
                SELECT e FROM Expense e
                WHERE e.user = :user
                    AND (:searchQuery IS NULL OR LOWER(e.name) LIKE :searchQuery)
                    AND (:categoryIds IS NULL OR e.category.id IN :categoryIds OR e.category.parent.id IN :categoryIds)
                    AND (TRUE = :#{#startDate == null} or e.date >= :startDate)
                    AND (TRUE = :#{#endDate == null} or e.date <= :endDate)
                ORDER BY e.date DESC
            """)
    Page<Expense> findByUserAndOptionalDateRange(
            @Param("user") User user,
            @Param("searchQuery") String searchQuery,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("""
                SELECT COALESCE(SUM(e.amount), 0)
                FROM Expense e
                WHERE e.user = :user
                  AND e.date >= :startDate
                  AND e.date <= :endDate
                  AND e.account.id IN :accountIds
            """)
    BigDecimal getTotalAmountForUserAndAccounts(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("accountIds") List<Long> accountIds);

    List<Expense> findTop5ByUserAndAccountIdInAndDateAfterOrderByDateDesc(
            User user,
            List<Long> accountIds,
            LocalDate startDate);

    @Query("""
                SELECT e FROM Expense e
                WHERE e.account.id = :accountId
                    AND (TRUE = :#{#startDate == null} OR e.date >= :startDate)
                    AND (TRUE = :#{#endDate == null} OR e.date <= :endDate)
                ORDER BY e.date ASC
            """)
    List<Expense> findByAccountIdAndOptionalDates(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT new com.dorsetsoftware.PennyPal.category.dto.CategoryExpenseSummaryDto(
                    COALESCE(parent.id, c.id),
                    COALESCE(parent.name, c.name),
                    ABS(SUM(e.amount))
                )
                FROM Expense e
                JOIN e.category c
                LEFT JOIN c.parent parent
                WHERE e.user = :user
                  AND e.date >= :startDate
                  AND e.account.id IN :accountIds
                  AND COALESCE(parent.type, c.type) = :categoryType
                GROUP BY COALESCE(parent.id, c.id), COALESCE(parent.name, c.name)
                HAVING SUM(e.amount) < 0
            """)
    List<CategoryExpenseSummaryDto> findRootCategoryTotalsForUserSinceAndAccountIds(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("accountIds") List<Long> accountIds,
            @Param("categoryType") CategoryType categoryType);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.account.id = :accountId
                AND (TRUE = :#{#startDate == null} or e.date < :startDate)
                """)
    BigDecimal sumAmountBeforeDate(@Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate);

    @Query("SELECT MIN(e.date) FROM Expense e WHERE e.account.id = :accountId")
    LocalDate findEarliestDateForAccount(@Param("accountId") Long accountId);

    @Query("SELECT MIN(e.date) FROM Expense e WHERE e.user.username = :username")
    LocalDate findEarliestDateForUser(@Param("username") String username);
}
