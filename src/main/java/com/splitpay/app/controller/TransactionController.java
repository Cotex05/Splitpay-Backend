package com.splitpay.app.controller;

import com.splitpay.app.model.Transaction;
import com.splitpay.app.payload.dto.TransactionDTO;
import com.splitpay.app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

//    @PostMapping("/groups/{groupId}")
//    public ResponseEntity<List<TransactionDTO>> createTransaction(@PathVariable Long groupId, @RequestParam Long payeeId){
//        List<TransactionDTO> transactions = transactionService.createTransaction(groupId, payeeId);
//        return new ResponseEntity<>(transactions, HttpStatus.CREATED);
//    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsOfGroup(@PathVariable Long groupId){
        List<TransactionDTO> transactionDTOList = transactionService.getAllTransactionsOfGroup(groupId);
        return new ResponseEntity<>(transactionDTOList, HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/user")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsOfUserInGroup(@PathVariable Long groupId){
        List<TransactionDTO> transactionDTOList = transactionService.getAllTransactionsOfUserInGroup(groupId);
        return new ResponseEntity<>(transactionDTOList, HttpStatus.OK);
    }
}
