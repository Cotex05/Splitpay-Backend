package com.splitpay.app.service;

import com.splitpay.app.exception.APIException;
import com.splitpay.app.exception.ResourceNotFoundException;
import com.splitpay.app.model.PaymentChannel;
import com.splitpay.app.model.UpiAddress;
import com.splitpay.app.model.User;
import com.splitpay.app.payload.dto.UpiAddressDTO;
import com.splitpay.app.repository.PaymentChannelRepository;
import com.splitpay.app.repository.UpiAddressRepository;
import com.splitpay.app.repository.UserRepository;
import com.splitpay.app.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UpiAddressServiceImpl implements UpiAddressService {

    @Autowired
    private PaymentChannelRepository paymentChannelRepository;

    @Autowired
    private UpiAddressRepository upiAddressRepository;

    @Autowired
    private AuthUtil authUtil;

    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private UserRepository userRepository;

    @Override
    public UpiAddressDTO addUpiAddress(String upi) {
        User currUser = authUtil.loggedInUser();
        PaymentChannel paymentChannel = paymentChannelRepository.findByUserIdAndPaymentChannelType(currUser.getUserId(), "UPI");
        if (paymentChannel == null) {
            throw new APIException("Unable to find payment channel for UPI!");
        }
        UpiAddress upiAddress = new UpiAddress();
        upiAddress.setUpiAddress(upi);
        upiAddress.setPaymentChannel(paymentChannel);
        upiAddress.setCreatedAt(LocalDateTime.now());
        UpiAddress savedUpiAddress = upiAddressRepository.save(upiAddress);
        return modelMapper.map(savedUpiAddress, UpiAddressDTO.class);
    }

    @Transactional
    @Override
    public UpiAddressDTO updateUpiAddress(String upi, Long upiAddressId) {
        User currUser = authUtil.loggedInUser();
        UpiAddress upiAddress = upiAddressRepository.findById(upiAddressId)
                        .orElseThrow(() -> new ResourceNotFoundException("UpiAddress", "upiAddressId", upiAddressId));
        PaymentChannel paymentChannel = paymentChannelRepository.findById(upiAddress.getPaymentChannel().getPaymentChannelId())
                        .orElseThrow(() -> new ResourceNotFoundException("PaymentChannel", "paymentChannelId", upiAddress.getPaymentChannel().getPaymentChannelId()));
        if(!Objects.equals(paymentChannel.getUser().getUserId(), currUser.getUserId())) {
            throw new APIException("User not allowed to update UPI address!");
        }
        upiAddress.setUpiAddress(upi);
        UpiAddress savedUpiAddress = upiAddressRepository.save(upiAddress);
        return modelMapper.map(savedUpiAddress, UpiAddressDTO.class);
    }

    @Override
    public void removeUpiAddress(Long upiAddressId) {
        User currUser = authUtil.loggedInUser();
        UpiAddress upiAddress = upiAddressRepository.findById(upiAddressId)
                .orElseThrow(() -> new ResourceNotFoundException("UpiAddress", "upiAddressId", upiAddressId));
        PaymentChannel paymentChannel = paymentChannelRepository.findById(upiAddress.getPaymentChannel().getPaymentChannelId())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentChannel", "paymentChannelId", upiAddress.getPaymentChannel().getPaymentChannelId()));
        if(!Objects.equals(paymentChannel.getUser().getUserId(), currUser.getUserId())) {
            throw new APIException("User not allowed to remove UPI address!");
        }
        upiAddressRepository.deleteById(upiAddressId);
    }

    @Override
    public List<UpiAddressDTO> getUpiAddressDetailsByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<UpiAddress> upiAddressList = upiAddressRepository.findAllByUserId(user.getUserId());
        List<UpiAddressDTO> upiAddressDTOList = upiAddressList.stream()
                .map((upiAddress -> modelMapper.map(upiAddress, UpiAddressDTO.class)))
                .toList();
        return upiAddressDTOList;
    }
}
