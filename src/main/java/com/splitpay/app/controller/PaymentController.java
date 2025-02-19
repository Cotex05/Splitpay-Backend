package com.splitpay.app.controller;

import com.splitpay.app.model.Payment;
import com.splitpay.app.payload.PaymentRequest;
import com.splitpay.app.payload.dto.PaymentDTO;
import com.splitpay.app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/operations/create")
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentDTO paymentDTO = paymentService.createPayment(paymentRequest);
        return new ResponseEntity<>(paymentDTO, HttpStatus.CREATED);
    }

    @GetMapping("/groups/{groupId}/user")
    public ResponseEntity<List<PaymentDTO>> getUserPaymentsInGroup(@PathVariable Long groupId) {
        List<PaymentDTO> paymentDTOList = paymentService.getUserPaymentsInGroup(groupId);
        return new ResponseEntity<>(paymentDTOList, HttpStatus.OK);
    }

}
