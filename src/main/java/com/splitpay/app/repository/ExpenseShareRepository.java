package com.splitpay.app.repository;

import com.splitpay.app.model.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    @Query("SELECT ES FROM ExpenseShare ES WHERE ES.expense.group.groupId = ?1 AND ES.user.userId = ?2 AND ES.expense.paidBy.userId = ?3 AND ES.settled = false")
    List<ExpenseShare> findAllByGroupIdAndUserIdAndUnSettled(Long groupId, Long payerId, Long payeeId);
}
