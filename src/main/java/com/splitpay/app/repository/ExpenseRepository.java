package com.splitpay.app.repository;

import com.splitpay.app.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT e FROM Expense e where e.group.groupId = ?1 order by e.createdAt desc")
    List<Expense> findAllByGroupId(Long groupId);

    @Query("SELECT e FROM Expense e where e.group.groupId = ?1 order by e.createdAt desc")
    Page<Expense> findAllByGroupId(Long groupId, Pageable pageable);

    @Query("SELECT E FROM Expense E WHERE E.group.groupId = ?1 AND E.paidBy.userId = ?2")
    List<Expense> findAllByGroupIdAndUserId(Long groupId, Long userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paidBy.userId = :userId")
    Double findTotalAmountByUserId(Long userId);

    @Query("SELECT function('DATE', e.createdAt), SUM(e.amount) FROM Expense e " +
            "WHERE e.paidBy.userId = :userId AND e.createdAt >= :startDate " +
            "GROUP BY function('DATE', e.createdAt) ORDER BY FUNCTION('DATE', e.createdAt) ASC")
    List<Object[]> findAllByUserIdAndLastWeek(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
