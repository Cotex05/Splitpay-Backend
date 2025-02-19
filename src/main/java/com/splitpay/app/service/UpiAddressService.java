package com.splitpay.app.service;

import com.splitpay.app.payload.dto.UpiAddressDTO;

import java.util.List;

public interface UpiAddressService {
    UpiAddressDTO addUpiAddress(String upi);

    UpiAddressDTO updateUpiAddress(String upi, Long upiAddressId);

    void removeUpiAddress(Long upiAddressId);

    List<UpiAddressDTO> getUpiAddressDetailsByUsername(String username);
}
