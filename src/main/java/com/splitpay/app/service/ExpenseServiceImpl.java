package com.splitpay.app.service;

import com.splitpay.app.exception.APIException;
import com.splitpay.app.exception.ResourceNotFoundException;
import com.splitpay.app.model.*;
import com.splitpay.app.payload.*;
import com.splitpay.app.payload.dto.ExpenseDTO;
import com.splitpay.app.payload.dto.ExpenseShareDTO;
import com.splitpay.app.payload.dto.GroupDTO;
import com.splitpay.app.payload.dto.UserDTO;
import com.splitpay.app.repository.*;
import com.splitpay.app.util.AuthUtil;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Autowired
    private AuthUtil authUtil;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    @Transactional
    @Override
    public ExpenseDTO addExpense(Long groupId, ExpenseRequest expenseRequest) {
        // who pays the whole expense
        User payer = authUtil.loggedInUser();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        userService.checkUserExistsInGroup(groupId);

        Expense expense = new Expense();
        expense.setDescription(expenseRequest.getDescription());

        expense.setGroup(group);
        expense.setPaidBy(payer);
        expense.setCategory(expenseRequest.getCategory());
        expense.setAmount(expenseRequest.getAmount());
        expense.setCreatedAt(expenseRequest.getCreatedAt());

        Expense savedExpense = expenseRepository.save(expense);

        // Equal distribution b/w all group members
//        List<GroupMember> groupMembers = group.getMembers();
//
//        Integer totalMembers = groupMembers.size();
//        double sharedAmount = expenseRequest.getAmount() / totalMembers;
//
//        List<ExpenseShare> shares = new ArrayList<>();
//        for (GroupMember gm : groupMembers) {
//            if (gm.getUser().equals(payer)) {
//                continue;
//            }
//            ExpenseShare expenseShare = new ExpenseShare();
//            expenseShare.setExpense(savedExpense);
//            expenseShare.setUser(gm.getUser());
//            expenseShare.setAmount(sharedAmount); // Payer owes 0
//            expenseShare.setSettled(false);
//            shares.add(expenseShare);
//        }
//        expenseShareRepository.saveAll(shares);
//
//        List<ExpenseShareDTO> sharesDTOs = shares.stream()
//                .map((share) -> modelMapper.map(share, ExpenseShareDTO.class))
//                .toList();
        // END

        // Distribution according to sharedUsers
        Integer totalMembers = expenseRequest.getSharedUsers().size();
        if (totalMembers == 0) {
            throw new APIException("Expense Shares not added to group!");
        }
        double sharedAmount = expenseRequest.getAmount() / totalMembers;

        List<ExpenseShare> shares = new ArrayList<>();
        for (Long userId : expenseRequest.getSharedUsers()) {
            User owedUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            // handling the case for payer itself
            if (owedUser.equals(payer)) {
                ExpenseShare expenseShare = new ExpenseShare();
                expenseShare.setExpense(savedExpense);
                expenseShare.setUser(owedUser);
                expenseShare.setAmount(0.0); // Payer owes 0
                expenseShare.setSettled(true);
                shares.add(expenseShare);
                continue;
            }
            ExpenseShare expenseShare = new ExpenseShare();
            expenseShare.setExpense(savedExpense);
            expenseShare.setUser(owedUser);
            // Also adding share in round figures upto 2 decimal
            expenseShare.setAmount((Math.round(sharedAmount) * 100) / 100.0);
            expenseShare.setSettled(false);
            shares.add(expenseShare);
        }
        expenseShareRepository.saveAll(shares);
        List<ExpenseShareDTO> sharesDTOs = shares.stream()
                .map((share) -> modelMapper.map(share, ExpenseShareDTO.class))
                .toList();

        ExpenseDTO expenseDTO = modelMapper.map(savedExpense, ExpenseDTO.class);

        expenseDTO.setShares(sharesDTOs);
        return expenseDTO;
    }

    @Transactional
    @Override
    public void removeExpense(Long expenseId, Long groupId) {
        User currUser = authUtil.loggedInUser();
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));

        userService.checkUserExistsInGroup(groupId);

        if (!Objects.equals(currUser.getUserId(), expense.getPaidBy().getUserId())) {
            throw new APIException("You are not allowed to delete expense added by " + expense.getPaidBy().getUserName());
        }
        expenseRepository.delete(expense);
    }

    @Override
    public ExpenseResponse getAllExpensesFromGroup(Long groupId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Specification<Expense> spec = Specification.where(null);
        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("groupId"), groupId));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        userService.checkUserExistsInGroup(groupId);

        Page<Expense> expensePage = expenseRepository.findAllByGroupId(groupId, pageDetails);
        List<Expense> expenses = expensePage.getContent();
        List<ExpenseDTO> expenseDTOs = expenses.stream()
                .map(expense -> modelMapper.map(expense, ExpenseDTO.class))
                .toList();
        ExpenseResponse expenseResponse = new ExpenseResponse();
        expenseResponse.setContent(expenseDTOs);
        expenseResponse.setPageNumber(expensePage.getNumber());
        expenseResponse.setPageSize(expensePage.getSize());
        expenseResponse.setTotalElements(expensePage.getTotalElements());
        expenseResponse.setTotalPages(expensePage.getTotalPages());
        expenseResponse.setLastPage(expensePage.isLast());
        return expenseResponse;
    }

    @Override
    public List<ExpenseDTO> getUsersExpensesFromGroup(Long groupId) {
        User currUser = authUtil.loggedInUser();
        List<Expense> expenses = expenseRepository.findAllByGroupIdAndUserId(groupId, currUser.getUserId());
        return expenses.stream()
                .map((expense -> modelMapper.map(expense, ExpenseDTO.class)))
                .toList();
    }

    @Override
    public ExpenseDTO getExpense(Long expenseId, Long groupId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));

        userService.checkUserExistsInGroup(groupId);
        return modelMapper.map(expense, ExpenseDTO.class);

    }

    @Override
    public List<ExpenseBalanceResponse> getExpenseBalanceOfGroup(Long groupId) {

        userService.checkUserExistsInGroup(groupId);

        List<Expense> expenses = expenseRepository.findAllByGroupId(groupId);

        // Map every transaction for each user
        List<ExpenseBalanceResponse> detailedBalances = new ArrayList<>();

        for (Expense expense : expenses) {
            ExpenseBalanceResponse expenseBalanceResponse = new ExpenseBalanceResponse();
            User paidBy = expense.getPaidBy();
            String toUserName = paidBy.getUserName();
            expenseBalanceResponse.setUserName(toUserName);
            expenseBalanceResponse.setUserId(paidBy.getUserId());
            List<ExpenseShare> expenseShares = expense.getShares();
            List<TransactionDetail> transactionDetails = new ArrayList<>();
            for (ExpenseShare expenseShare : expenseShares) {
                if (expenseShare.getSettled()) {
                    continue;
                }
                String fromUserName = expenseShare.getUser().getUserName();
                double amount = expenseShare.getAmount();
                TransactionDetail transactionDetail = new TransactionDetail();
                transactionDetail.setFromUserName(fromUserName);
                transactionDetail.setToUserName(toUserName);
                transactionDetail.setAmount(amount);
                transactionDetails.add(transactionDetail);
            }
            expenseBalanceResponse.setTransactions(transactionDetails);
            detailedBalances.add(expenseBalanceResponse);
        }

        return detailedBalances;
    }

    @Override
    public List<BalanceGraphResponse> getBalanceGraphOfGroup(Long groupId) {
        List<ExpenseBalanceResponse> detailedBalances = getExpenseBalanceOfGroup(groupId);
        Map<PairExpense, Double> balances = new HashMap<>();
        for (ExpenseBalanceResponse expenseBalanceResponse : detailedBalances) {
            List<TransactionDetail> transactions = expenseBalanceResponse.getTransactions();
            for (TransactionDetail transaction : transactions) {
                PairExpense pairExpense = new PairExpense();
                pairExpense.setFromUserName(transaction.getFromUserName());
                pairExpense.setToUserName(transaction.getToUserName());
                if (balances.containsKey(pairExpense)) {
                    balances.put(pairExpense, balances.get(pairExpense) + transaction.getAmount());
                } else {
                    balances.put(pairExpense, transaction.getAmount());
                }
            }
        }

        Map<PairExpense, Double> finalBalances = new HashMap<>();
        Map<PairExpense, Boolean> visited = new HashMap<>();

        for (Map.Entry<PairExpense, Double> entry : balances.entrySet()) {
            PairExpense pairExpense1 = entry.getKey();
            PairExpense pairExpense2 = new PairExpense();
            pairExpense2.setFromUserName(pairExpense1.getToUserName());
            pairExpense2.setToUserName(pairExpense1.getFromUserName());
            if (visited.containsKey(pairExpense1) || visited.containsKey(pairExpense2)) {
                continue;
            }
            if (balances.containsKey(pairExpense2)) {
                if (balances.get(pairExpense2) > entry.getValue()) {
                    finalBalances.put(pairExpense2, balances.get(pairExpense2) - entry.getValue());
                    visited.put(pairExpense2, true);
                } else {
                    finalBalances.put(pairExpense1, entry.getValue() - balances.get(pairExpense2));
                    visited.put(pairExpense1, true);
                }
            } else {
                finalBalances.put(pairExpense1, entry.getValue());
                visited.put(pairExpense1, true);
            }
        }

        List<BalanceGraphResponse> balanceGraphResponseList = new ArrayList<>();

        // Map the transactions required to settle payment as balance graph
        for (Map.Entry<PairExpense, Double> entry : finalBalances.entrySet()) {
            PairExpense pairExpense = entry.getKey();
            String fromUserName = pairExpense.getFromUserName();
            User payer = userRepository.findByUserName(fromUserName).orElseThrow(() -> new ResourceNotFoundException("User", "name", fromUserName));

            String toUserName = pairExpense.getToUserName();
            User payee = userRepository.findByUserName(toUserName).orElseThrow(() -> new ResourceNotFoundException("User", "name", toUserName));

            Double amount = entry.getValue();

            BalanceGraphResponse balanceGraphResponse = new BalanceGraphResponse();
            balanceGraphResponse.setPayer(modelMapper.map(payer, UserDTO.class));
            balanceGraphResponse.setPayee(modelMapper.map(payee, UserDTO.class));
            balanceGraphResponse.setAmount(amount);

            balanceGraphResponseList.add(balanceGraphResponse);
        }

        return balanceGraphResponseList;
    }

    @Override
    public UserBalanceGraphResponse getUserBalanceGraphOfGroup(Long groupId) {
        User currUser = authUtil.loggedInUser();
        UserBalanceGraphResponse userBalanceGraphResponse = new UserBalanceGraphResponse();
        List<BalanceGraphResponse> balanceGraphResponseList = getBalanceGraphOfGroup(groupId);
        List<BalanceGraphResponse> toSend = balanceGraphResponseList.stream()
                .filter((balanceGraphResponse -> balanceGraphResponse.getPayer().getUserId().equals(currUser.getUserId())))
                .toList();
        List<BalanceGraphResponse> toReceive = balanceGraphResponseList.stream()
                .filter((balanceGraphResponse -> balanceGraphResponse.getPayee().getUserId().equals(currUser.getUserId())))
                .toList();
        userBalanceGraphResponse.setToSend(toSend);
        userBalanceGraphResponse.setToReceive(toReceive);
        return userBalanceGraphResponse;
    }

    @Override
    public BalanceResponse getUserBalanceInGroup(Long groupId) {
        BalanceResponse balanceResponse = new BalanceResponse();
        UserBalanceGraphResponse userBalanceGraphResponse = getUserBalanceGraphOfGroup(groupId);
        Double cashInFlow = 0.0;
        Double cashOutFlow = 0.0;
        for (BalanceGraphResponse balanceGraphResponse : userBalanceGraphResponse.getToReceive()) {
            cashInFlow += balanceGraphResponse.getAmount();
        }
        for (BalanceGraphResponse balanceGraphResponse : userBalanceGraphResponse.getToSend()) {
            cashOutFlow += balanceGraphResponse.getAmount();
        }
        balanceResponse.setCashIn(Math.round((cashInFlow * 100)) / 100.0);
        balanceResponse.setCashOut(Math.round(cashOutFlow * 100) / 100.0);
        return balanceResponse;
    }

    @Override
    public ExpenseStatsResponse getUserExpenseStats() {
        User currUser = authUtil.loggedInUser();

        List<GroupDTO> groupDTOList = groupService.getUsersAllGroups();

        Double totalAmountSpent = 0.0;
        Double totalAmount = expenseRepository.findTotalAmountByUserId(currUser.getUserId());
        if (totalAmount != null) {
            totalAmountSpent = totalAmount;
        }
        Double totalAmountIn = 0.0;
        Double totalAmountOut = 0.0;

        for (GroupDTO groupDTO : groupDTOList) {
            BalanceResponse balanceResponse = getUserBalanceInGroup(groupDTO.getGroupId());
            totalAmountIn += balanceResponse.getCashIn();
            totalAmountOut += balanceResponse.getCashOut();
        }


        ExpenseStatsResponse expenseStatsResponse = new ExpenseStatsResponse();

        expenseStatsResponse.setTotalAmountIn(totalAmountIn);
        expenseStatsResponse.setTotalAmountOut(totalAmountOut);
        expenseStatsResponse.setTotalAmountSpent(totalAmountSpent);

        return expenseStatsResponse;
    }

    @Override
    public List<WeeklyExpenseStatsResponse> getWeeklyUserExpenseStats() {
        List<WeeklyExpenseStatsResponse> weeklyExpenseStatsResponseList = new ArrayList<>();
        User currUser = authUtil.loggedInUser();
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Object[]> expenses = expenseRepository.findAllByUserIdAndLastWeek(currUser.getUserId(), oneWeekAgo);
        for (Object[] expense : expenses) {
            WeeklyExpenseStatsResponse weeklyExpenseStatsResponse = new WeeklyExpenseStatsResponse();
            LocalDate createdAt = LocalDate.parse(expense[0].toString());
            DayOfWeek dayOfWeek = createdAt.getDayOfWeek();

            String dayName = dayOfWeek.toString().charAt(0) + dayOfWeek.toString().substring(1, 3).toLowerCase();
            weeklyExpenseStatsResponse.setDate(createdAt);
            weeklyExpenseStatsResponse.setSpentAmount((Double) expense[1]);
            weeklyExpenseStatsResponse.setWeekday(dayName);
            weeklyExpenseStatsResponseList.add(weeklyExpenseStatsResponse);
        }
        return weeklyExpenseStatsResponseList;
    }


}



