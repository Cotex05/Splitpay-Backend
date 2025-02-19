package com.splitpay.app.service;

import com.splitpay.app.model.Payment;
import com.splitpay.app.payload.PaymentRequest;
import com.splitpay.app.payload.dto.PaymentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PaymentService {
    PaymentDTO createPayment(PaymentRequest paymentRequest);

    List<PaymentDTO> getUserPaymentsInGroup(Long groupId);
}
