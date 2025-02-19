package com.splitpay.app.controller;

import com.splitpay.app.model.UpiAddress;
import com.splitpay.app.payload.dto.UpiAddressDTO;
import com.splitpay.app.repository.PaymentChannelRepository;
import com.splitpay.app.repository.UpiAddressRepository;
import com.splitpay.app.service.UpiAddressService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/upi")
public class UpiAddressController {

    @Autowired
    private UpiAddressService upiAddressService;

    @PostMapping("/address/add")
    public ResponseEntity<UpiAddressDTO> addUpiAddress(@RequestParam String upi) {
        UpiAddressDTO upiAddressDTO = upiAddressService.addUpiAddress(upi);
        return new ResponseEntity<>(upiAddressDTO, HttpStatus.CREATED);
    }

    @PutMapping("/address/{upiAddressId}")
    public ResponseEntity<UpiAddressDTO> updateUpiAddress(@RequestParam String upi, @PathVariable Long upiAddressId) {
        UpiAddressDTO upiAddressDTO = upiAddressService.updateUpiAddress(upi, upiAddressId);
        return new ResponseEntity<>(upiAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/address/{upiAddressId}")
    public ResponseEntity<String> removeUpiAddress(@PathVariable Long upiAddressId) {
        upiAddressService.removeUpiAddress(upiAddressId);
        return new ResponseEntity<>("Upi address deleted successfully!", HttpStatus.OK);
    }

    @GetMapping("/address/users/{username}")
    public ResponseEntity<List<UpiAddressDTO>> getUpiAddressesByUsername(@PathVariable String username) {
        List<UpiAddressDTO> upiAddressList = upiAddressService.getUpiAddressDetailsByUsername(username);
        return new ResponseEntity<>(upiAddressList, HttpStatus.OK);
    }

}
