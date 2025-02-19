package com.splitpay.app.service;

import com.splitpay.app.model.Group;
import com.splitpay.app.model.Transaction;
import com.splitpay.app.model.User;
import com.splitpay.app.payload.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {

    List<TransactionDTO> createTransaction(Group group, User payer, User payee);

    List<TransactionDTO> getAllTransactionsOfGroup(Long groupId);

    List<TransactionDTO> getAllTransactionsOfUserInGroup(Long groupId);

}
