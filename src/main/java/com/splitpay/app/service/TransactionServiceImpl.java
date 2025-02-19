package com.splitpay.app.service;

import com.splitpay.app.exception.APIException;
import com.splitpay.app.model.*;
import com.splitpay.app.payload.dto.TransactionDTO;
import com.splitpay.app.repository.*;
import com.splitpay.app.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Autowired
    private AuthUtil authUtil;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseService expenseService;

    @Transactional
    @Override
    public List<TransactionDTO> createTransaction(Group group, User payer, User payee) {

        List<Transaction> transactions = new ArrayList<>();

        List<ExpenseShare> expenseShares = expenseShareRepository.findAllByGroupIdAndUserIdAndUnSettled(group.getGroupId(), payer.getUserId(), payee.getUserId());

        for(ExpenseShare expenseShare : expenseShares) {
            Transaction transaction = new Transaction();
            transaction.setPayer(payer);

            transaction.setPayee(payee);
            transaction.setExpenseShare(expenseShare);
            transaction.setAmount(expenseShare.getAmount());
            // mark this expense share as settled
            expenseShare.setSettled(true);
            expenseShareRepository.save(expenseShare);
            Transaction savedTransaction = transactionRepository.save(transaction);
            transactions.add(savedTransaction);
        }

        return transactions.stream()
                .map((transaction -> modelMapper.map(transaction, TransactionDTO.class)))
                .toList();
    }

    @Override
    public List<TransactionDTO> getAllTransactionsOfGroup(Long groupId) {
        userService.checkUserExistsInGroup(groupId);
        List<Transaction> transactions = transactionRepository.findAllByGroupId(groupId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .toList();
        return transactionDTOs;
    }

    @Override
    public List<TransactionDTO> getAllTransactionsOfUserInGroup(Long groupId) {
        userService.checkUserExistsInGroup(groupId);
        User currUser = authUtil.loggedInUser();
        List<Transaction> transactions = transactionRepository.findAllByGroupIdAndUserId(groupId, currUser.getUserId());
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .toList();
        return transactionDTOs;
    }
}
