package com.dorsetsoftware.PennyPal.transfer.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dorsetsoftware.PennyPal.transfer.entity.Transfer;
import com.dorsetsoftware.PennyPal.user.entity.User;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("""
                SELECT t FROM Transfer t
                WHERE t.user = :user
                    AND (TRUE = :#{#startDate == null} or t.date >= :startDate)
                    AND (TRUE = :#{#endDate == null} or t.date <= :endDate)
            """)
    Page<Transfer> findByUserAndOptionalDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    List<Transfer> findTop5ByUserAndDateAfterOrderByDateDesc(
            User user,
            LocalDate startDate);
}
