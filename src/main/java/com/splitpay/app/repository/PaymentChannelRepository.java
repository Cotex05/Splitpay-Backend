package com.splitpay.app.repository;

import com.splitpay.app.model.PaymentChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentChannelRepository extends JpaRepository<PaymentChannel, Long> {
    @Query("SELECT P FROM PaymentChannel P WHERE P.user.userId = ?1 AND P.paymentChannelType = ?2")
    PaymentChannel findByUserIdAndPaymentChannelType(Long userId, String paymentChannelType);
}
