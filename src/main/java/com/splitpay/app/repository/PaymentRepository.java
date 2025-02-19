package com.splitpay.app.repository;

import com.splitpay.app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("Select P from Payment P where P.group.groupId = ?1")
    List<Payment> findAllByGroupId(Long groupId);

    @Query("Select P from Payment P where P.group.groupId = ?1 and (P.payee.userId = ?2 or P.payer.userId = ?2)")
    List<Payment> findAllByGroupIdAndUserId(Long groupId, Long userId);
}
