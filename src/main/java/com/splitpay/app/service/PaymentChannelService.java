package com.splitpay.app.service;

import com.splitpay.app.model.PaymentChannel;

public interface PaymentChannelService {
    PaymentChannel getPaymentChannel(long userId, String paymentChannelType);
}
