package com.example.splitExpenses;

import com.example.splitExpenses.dto.CashFlowDTO;
import com.example.splitExpenses.dto.FriendDTO;
import com.example.splitExpenses.dto.NewFriendDTO;
import com.example.splitExpenses.dto.NewPaymentDTO;
import com.example.splitExpenses.dto.PaymentDTO;
import com.example.splitExpenses.service.FriendAlreadyCreatedException;
import com.example.splitExpenses.service.FriendNotExistsException;
import com.example.splitExpenses.service.SplitExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SplitExpensesApplicationTests {

	private static ObjectMapper jsonMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SplitExpenseService splitExpenseService;

	@BeforeAll
	static void setUp() {
		jsonMapper = new ObjectMapper();
		jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Test
	void addFriendOkTest() throws Exception {
		// Given
		NewFriendDTO newFriendDTO = new NewFriendDTO("nombre1");

		// When
		ResultActions resultActions = this.mockMvc.perform(put("/SplitExpense/friend").
				contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(newFriendDTO)));
		// Then
		resultActions.andExpect(status().isOk());
	}

	@Test
	void addFriendAlreadyCreatedTest() throws Exception {
		// Given
		NewFriendDTO newFriendDTO = new NewFriendDTO("nombre1");
		FriendDTO friendDTO = new FriendDTO("nombre1", 0);
		doThrow(new FriendAlreadyCreatedException()).when(splitExpenseService).addFriend(friendDTO);
		// When
		ResultActions resultActions = this.mockMvc.perform(put("/SplitExpense/friend").
				contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(newFriendDTO)));
		// Then
		resultActions.andExpect(status().isConflict());
	}

	@Test
	void addPaymentOkTest() throws Exception {
		// Given
		NewPaymentDTO newPaymentDTO = new NewPaymentDTO(50, "lala");
		// When
		ResultActions resultActions = this.mockMvc.perform(post("/SplitExpense/friend/nombre/payment").
				contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(newPaymentDTO)));
		// Then
		resultActions.andExpect(status().isOk());
	}

	@Test
	void addPaymentFriendNotFoundTest() throws Exception {
		// Given
		NewPaymentDTO newPaymentDTO = new NewPaymentDTO(50, "lala");
		PaymentDTO paymentDTO = new PaymentDTO("nombre", 50, "lala", null);
		doThrow(new FriendNotExistsException()).when(splitExpenseService).addPayment(paymentDTO);
		// When
		ResultActions resultActions = this.mockMvc.perform(post("/SplitExpense/friend/nombre/payment").
				contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(newPaymentDTO)));
		// Then
		resultActions.andExpect(status().isNotFound());
	}

	@Test
	void getBalanceTest() throws Exception {
		// Given
		FriendDTO friendDTO1 = new FriendDTO("name1", 3);
		FriendDTO friendDTO2 = new FriendDTO("name2", 2);
		FriendDTO friendDTO3 = new FriendDTO("name3", 1);
		List<FriendDTO> friends = Lists.newArrayList(friendDTO1, friendDTO2, friendDTO3);
		when(splitExpenseService.getBalance()).thenReturn(friends);
		// When
		ResultActions resultActions = this.mockMvc.perform(get("/SplitExpense/balance"));
		// Then
		resultActions.andExpect(status().isOk())
					 .andExpect(content().json(jsonMapper.writeValueAsString(friends)));
	}

	@Test
	void getCashFlowsTest() throws Exception {
		// Given
		CashFlowDTO cashFlowDTO1 = new CashFlowDTO("name1", "name2", 1);
		CashFlowDTO cashFlowDTO2 = new CashFlowDTO("name3", "name2", 2);
		CashFlowDTO cashFlowDTO3 = new CashFlowDTO("name1", "name3", 3);

		List<CashFlowDTO> cashFlowDTOS = Lists.newArrayList(cashFlowDTO1, cashFlowDTO2, cashFlowDTO3);
		when(splitExpenseService.getCashFlows()).thenReturn(cashFlowDTOS);
		// When
		ResultActions resultActions = this.mockMvc.perform(get("/SplitExpense/cashFlows"));
		// Then
		resultActions.andExpect(status().isOk())
				.andExpect(content().json(jsonMapper.writeValueAsString(cashFlowDTOS)));
	}

	@Test
	void getPaymentsTest() throws Exception {
		// Given
		PaymentDTO paymentDTO1 = new PaymentDTO("name1", 3, "lala1", new Date());
		PaymentDTO paymentDTO2 = new PaymentDTO("name2", 2, "lala2", new Date());
		PaymentDTO paymentDTO3 = new PaymentDTO("name3", 1, "lala3", new Date());
		List<PaymentDTO> payments = Lists.newArrayList(paymentDTO1, paymentDTO2, paymentDTO3);
		when(splitExpenseService.getPayments()).thenReturn(payments);
		// When
		ResultActions resultActions = this.mockMvc.perform(get("/SplitExpense/payments"));
		// Then
		resultActions.andExpect(status().isOk())
				.andExpect(content().json(jsonMapper.writeValueAsString(payments)));
	}
}
