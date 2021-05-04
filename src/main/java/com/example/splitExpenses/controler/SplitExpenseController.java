package com.example.splitExpenses.controler;

import com.example.splitExpenses.dto.CashFlowDTO;
import com.example.splitExpenses.dto.FriendDTO;
import com.example.splitExpenses.dto.NewFriendDTO;
import com.example.splitExpenses.dto.NewPaymentDTO;
import com.example.splitExpenses.dto.PaymentDTO;
import com.example.splitExpenses.mapper.SplitExpenseMapper;
import com.example.splitExpenses.persistence.Payment;
import com.example.splitExpenses.service.FriendAlreadyCreatedException;
import com.example.splitExpenses.service.FriendNotExistsException;
import com.example.splitExpenses.service.SplitExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController()
@RequestMapping("/SplitExpense")
public class SplitExpenseController {

    @Autowired
    private SplitExpenseService splitExpenseService;

    @Autowired
    private SplitExpenseMapper splitExpenseMapper;

    @PutMapping("/friend")
    public ResponseEntity<Void> addFriend(@RequestBody NewFriendDTO friendDTO){
        try {
            splitExpenseService.addFriend(splitExpenseMapper.newFriendDTOToFriendDTO(friendDTO));
            return ResponseEntity.ok().build();
        } catch (FriendAlreadyCreatedException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/friend/{name}/payment")
    public ResponseEntity<Void> addPayment(@PathVariable String name, @RequestBody NewPaymentDTO paymentDTO) {
        try {
            splitExpenseService.addPayment(splitExpenseMapper.newPaymentDTOToPaymentDTO(name, paymentDTO));
        } catch (FriendNotExistsException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getPayments() {
        return ResponseEntity.ok(splitExpenseService.getPayments()) ;
    }


    @GetMapping("/balance")
    public ResponseEntity<List<FriendDTO>> getBalance() {
        return ResponseEntity.ok(splitExpenseService.getBalance()) ;
    }

    @GetMapping("/cashFlows")
    public ResponseEntity<List<CashFlowDTO>> getCashFlows() {
        return  ResponseEntity.ok(splitExpenseService.getCashFlows());
    }
}
