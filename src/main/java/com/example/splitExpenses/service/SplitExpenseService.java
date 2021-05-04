package com.example.splitExpenses.service;

import com.example.splitExpenses.dto.CashFlowDTO;
import com.example.splitExpenses.dto.FriendDTO;
import com.example.splitExpenses.dto.PaymentDTO;
import com.example.splitExpenses.mapper.SplitExpenseMapper;
import com.example.splitExpenses.persistence.Friend;
import com.example.splitExpenses.persistence.FriendRepository;
import com.example.splitExpenses.persistence.Payment;
import com.example.splitExpenses.persistence.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SplitExpenseService {
    @Autowired
    private SplitExpenseMapper splitExpenseMapper;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public void addFriend(FriendDTO friendDTO) throws FriendAlreadyCreatedException {
        if (friendRepository.existsFriendByName(friendDTO.getName())) {
            throw new FriendAlreadyCreatedException();
        }
        friendRepository.save(splitExpenseMapper.friendDtoToFriend(friendDTO));
    }

    public void addPayment(PaymentDTO paymentDTO) throws FriendNotExistsException {
        Optional<Integer> optionalId = friendRepository.getIdFromName(paymentDTO.getName());
        Integer id = optionalId.orElseThrow(FriendNotExistsException::new);
        paymentRepository.save(splitExpenseMapper.paymentDtoToPayment(paymentDTO, new Date(), id));
    }

    public List<PaymentDTO> getPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(splitExpenseMapper::paymentToPaymentDto).collect(Collectors.toList());
    }

    public List<FriendDTO> getBalance() {
        double totalPayments = paymentRepository.sumTotalPayments();
        List<Friend> friends = friendRepository.findAll();
        double paymentPerFriend = totalPayments / friends.size();

        Function<Friend, FriendDTO> createFriendBalance = friend -> {
             double balance = friend.getPayments().stream()
                .mapToDouble(Payment::getAmount).sum();
            return splitExpenseMapper.friendBalanceToFriendDTO(friend, balance - paymentPerFriend);
        };

        return friends.stream().map(createFriendBalance).
                sorted(Comparator.comparingDouble(FriendDTO::getBalance).reversed()).
                collect(Collectors.toList());
    }


    public List<CashFlowDTO> getCashFlows() {
        List<FriendDTO> friendDTOS = getBalance();
        List<CashFlowDTO> cashFlowDTOS = new ArrayList<>();

        int biggerBalanceIndex = 0;
        int lessBalanceIndex = friendDTOS.size() - 1;

        while (lessBalanceIndex > 0) {
            FriendDTO biggerBalanceFriend = friendDTOS.get(biggerBalanceIndex);
            FriendDTO lessBalanceFriend = friendDTOS.get(lessBalanceIndex);
            double amount;
            double biggerBalance = Math.abs(biggerBalanceFriend.getBalance());
            double lessBalance = Math.abs(lessBalanceFriend.getBalance());

            if (biggerBalance > lessBalance) {
                amount = lessBalance;
                friendDTOS.remove(lessBalanceIndex);
                lessBalanceIndex--;
                biggerBalanceFriend.setBalance(Double.sum(biggerBalanceFriend.getBalance(), lessBalanceFriend.getBalance()));
            } else if (biggerBalance < lessBalance) {
                amount = biggerBalance;
                friendDTOS.remove(biggerBalanceIndex);
                lessBalanceIndex--;
                lessBalanceFriend.setBalance(Double.sum(biggerBalanceFriend.getBalance(), lessBalanceFriend.getBalance()));
            } else {
                amount = biggerBalance;
                friendDTOS.remove(biggerBalanceIndex);
                lessBalanceIndex--;
                friendDTOS.remove(lessBalanceIndex);
                lessBalanceIndex--;
            }

            cashFlowDTOS.add(splitExpenseMapper.
                    namesAmountToCashFlowDTO(lessBalanceFriend.getName(), biggerBalanceFriend.getName(), amount));
        }

        return cashFlowDTOS;

    }


}
