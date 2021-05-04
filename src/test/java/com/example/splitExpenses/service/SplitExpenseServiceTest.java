package com.example.splitExpenses.service;

import com.example.splitExpenses.dto.CashFlowDTO;
import com.example.splitExpenses.dto.FriendDTO;
import com.example.splitExpenses.dto.PaymentDTO;
import com.example.splitExpenses.mapper.SplitExpenseMapperImpl;
import com.example.splitExpenses.persistence.Friend;
import com.example.splitExpenses.persistence.FriendRepository;
import com.example.splitExpenses.persistence.Payment;
import com.example.splitExpenses.persistence.PaymentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SplitExpenseServiceTest {

    @InjectMocks
    private SplitExpenseService splitExpenseService;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Spy
    private SplitExpenseMapperImpl splitExpenseMapper;

    private static FriendDTO friendDTO;

    private static PaymentDTO paymentDTO;

    private static Payment payment;

    private static Friend friend1;

    private static Friend friend2;

    @BeforeAll
    public static void init() {
        friendDTO = new FriendDTO("name", 1);
        paymentDTO = new PaymentDTO("name", 10, "desc", new Date());
        payment = new Payment(1, 12, "desc", new Date(), new Friend(1, "name", null));
        friend1 = new Friend(1, "name", Collections.singletonList(payment));
        friend2 = new Friend(2, "name2", Collections.EMPTY_LIST);
    }

    @Test
    public void addFriendTest() throws Exception {
        // Given
        when(friendRepository.existsFriendByName(friendDTO.getName())).thenReturn(false);
        InOrder inOrder = inOrder(friendRepository);
        // When
        splitExpenseService.addFriend(friendDTO);
        // Then
        inOrder.verify(friendRepository).existsFriendByName(friendDTO.getName());
        inOrder.verify(friendRepository).save(any(Friend.class));
    }

    @Test
    public void addFriendFriendAlreadyCreatedExceptionTest() {
        // Given
        when(friendRepository.existsFriendByName(friendDTO.getName())).thenReturn(true);
        // When
        // Then
        assertThrows(
                FriendAlreadyCreatedException.class, () -> splitExpenseService.addFriend(friendDTO));
    }

    @Test
    public void addPaymentTest() throws Exception {
        // Given
        when(friendRepository.getIdFromName(paymentDTO.getName())).thenReturn(Optional.of(1));
        InOrder inOrder = inOrder(friendRepository, paymentRepository);
        // When
        splitExpenseService.addPayment(paymentDTO);
        // Then
        inOrder.verify(friendRepository).getIdFromName(paymentDTO.getName());
        inOrder.verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    public void addPaymentFriendNotExistsExceptionTest() {
        // Given
        when(friendRepository.getIdFromName(paymentDTO.getName())).thenReturn(Optional.empty());
        // When
        assertThrows(
                FriendNotExistsException.class, () -> splitExpenseService.addPayment(paymentDTO));
        // Then
    }

    @Test
    public void getPaymentsTest() {
        // Given

        when(paymentRepository.findAll()).thenReturn(Collections.singletonList(payment));
        // When
        List<PaymentDTO> paymentDTOS = splitExpenseService.getPayments();
        // Then
        PaymentDTO paymentDTO = paymentDTOS.get(0);
        assertEquals(1, paymentDTOS.size());
        assertEquals(payment.getDescription(), paymentDTO.getDescription());
        assertEquals(payment.getFriend().getName(), paymentDTO.getName());
        assertEquals(payment.getAmount(), paymentDTO.getAmount());
        assertEquals(payment.getPaymentDate(), paymentDTO.getPaymentDate());
    }

    @Test
    public void getBalanceTest() {
        // Given
        when(paymentRepository.sumTotalPayments()).thenReturn(payment.getAmount());
        when(friendRepository.findAll()).thenReturn(Arrays.asList(friend1, friend2));
        InOrder inOrder = inOrder(friendRepository, paymentRepository);
        // When
        // When
        List<FriendDTO> friendDTOS = splitExpenseService.getBalance();
        // Then
        assertEquals(2, friendDTOS.size());
        FriendDTO friendDTO1 = friendDTOS.get(0);
        FriendDTO friendDTO2 = friendDTOS.get(1);
        assertEquals(friendDTO1.getName(), friend1.getName());
        assertEquals(friendDTO1.getBalance(),payment.getAmount() / friendDTOS.size());
        assertEquals(friendDTO2.getName(), friend2.getName());
        assertEquals(friendDTO2.getBalance(), -1 * (payment.getAmount() / friendDTOS.size()));
        inOrder.verify(paymentRepository).sumTotalPayments();
        inOrder.verify(friendRepository).findAll();
    }

    @Test
    public void getCashFlowsTest() {
        // Given
        when(paymentRepository.sumTotalPayments()).thenReturn(payment.getAmount());
        when(friendRepository.findAll()).thenReturn(Arrays.asList(friend1, friend2));
        InOrder inOrder = inOrder(friendRepository, paymentRepository);
        // When
        List<CashFlowDTO> cashFlowDTOS = splitExpenseService.getCashFlows();
        // Then
        assertEquals(1, cashFlowDTOS.size());
        CashFlowDTO cashFlowDTO = cashFlowDTOS.get(0);
        assertEquals(cashFlowDTO.getNameFrom(), friend2.getName());
        assertEquals(cashFlowDTO.getNameTo(), friend1.getName());
        assertEquals(cashFlowDTO.getAmount(), payment.getAmount() / 2);
        inOrder.verify(paymentRepository).sumTotalPayments();
        inOrder.verify(friendRepository).findAll();
    }
}
