package com.splitpay.app.repository;

import com.splitpay.app.model.UpiAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpiAddressRepository extends JpaRepository<UpiAddress, Long> {

    @Query("SELECT UPI FROM UpiAddress UPI WHERE UPI.paymentChannel.user.userId = ?1")
    List<UpiAddress> findAllByUserId(Long userId);

}
