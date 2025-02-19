package com.splitpay.app.service;

import com.splitpay.app.exception.APIException;
import com.splitpay.app.exception.ResourceNotFoundException;
import com.splitpay.app.model.Group;
import com.splitpay.app.model.Payment;
import com.splitpay.app.model.PaymentChannel;
import com.splitpay.app.model.User;
import com.splitpay.app.payload.BalanceGraphResponse;
import com.splitpay.app.payload.PaymentRequest;
import com.splitpay.app.payload.UserBalanceGraphResponse;
import com.splitpay.app.payload.dto.PaymentDTO;
import com.splitpay.app.repository.GroupRepository;
import com.splitpay.app.repository.PaymentChannelRepository;
import com.splitpay.app.repository.PaymentRepository;
import com.splitpay.app.repository.UserRepository;
import com.splitpay.app.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthUtil authUtil;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PaymentChannelRepository paymentChannelRepository;
    @Autowired
    private PaymentChannelService paymentChannelService;


    @Transactional
    @Override
    public PaymentDTO createPayment(PaymentRequest paymentRequest) {

        userService.checkUserExistsInGroup(paymentRequest.getGroupId());

        User payer = authUtil.loggedInUser();

        User payee = userRepository.findById(paymentRequest.getPayeeId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", paymentRequest.getPayeeId()));

        // find out group
        Group group = groupRepository.findById(paymentRequest.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "groupId", paymentRequest.getGroupId()));

        PaymentChannel paymentChannel = paymentChannelService.getPaymentChannel(payer.getUserId(), paymentRequest.getPaymentChannelType());

        UserBalanceGraphResponse userBalanceGraphResponse = expenseService.getUserBalanceGraphOfGroup(paymentRequest.getGroupId());

        Double amount = 0.0;

        for(BalanceGraphResponse balanceGraphResponse: userBalanceGraphResponse.getToSend()){
            if(balanceGraphResponse.getPayee().getUserId().equals(paymentRequest.getPayeeId())){
                amount += balanceGraphResponse.getAmount();
            }
        }

        if(amount==0.0){
            throw new APIException("You doesn't owes to user: " + payee.getUserName());
        }

        if(!amount.equals(paymentRequest.getAmount())){
            throw new APIException("Amount mismatch in system!");
        }

        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setReferenceId(UUID.randomUUID().toString());
        payment.setPayer(payer);
        payment.setPayee(payee);
        payment.setPaymentChannel(paymentChannel);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setGroup(group);

        Payment savedPayment = paymentRepository.save(payment);

        // create all transaction records for each expense share
        transactionService.createTransaction(group, payer, payee);
        transactionService.createTransaction(group, payee, payer);


        return modelMapper.map(savedPayment, PaymentDTO.class);

    }

    @Override
    public List<PaymentDTO> getUserPaymentsInGroup(Long groupId) {
        User currUser = authUtil.loggedInUser();
        List<Payment> payments = paymentRepository.findAllByGroupIdAndUserId(groupId, currUser.getUserId());
        return payments.stream().map(payment -> modelMapper.map(payment, PaymentDTO.class)).toList();
    }
}
