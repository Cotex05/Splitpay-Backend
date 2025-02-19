package com.splitpay.app.repository;

import com.splitpay.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT T FROM Transaction T where T.expenseShare.expense.group.groupId = ?1")
    List<Transaction> findAllByGroupId(Long groupId);

    @Query("SELECT T FROM Transaction T where T.expenseShare.expense.group.groupId = ?1 AND T.payer.userId = ?2")
    List<Transaction> findAllByGroupIdAndUserId(Long groupId, Long userId);
}
