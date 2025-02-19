package com.splitpay.app.service;

import com.splitpay.app.model.PaymentChannel;
import com.splitpay.app.repository.PaymentChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentChannelServiceImpl implements PaymentChannelService {

    @Autowired
    private PaymentChannelRepository paymentChannelRepository;

    @Override
    public PaymentChannel getPaymentChannel(long userId, String paymentChannelType) {
        return paymentChannelRepository.findByUserIdAndPaymentChannelType(userId, paymentChannelType);

    }
}
