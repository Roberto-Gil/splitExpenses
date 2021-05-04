package com.example.splitExpenses.mapper;

import com.example.splitExpenses.dto.CashFlowDTO;
import com.example.splitExpenses.dto.FriendDTO;
import com.example.splitExpenses.dto.NewFriendDTO;
import com.example.splitExpenses.dto.NewPaymentDTO;
import com.example.splitExpenses.dto.PaymentDTO;
import com.example.splitExpenses.persistence.Friend;
import com.example.splitExpenses.persistence.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Date;

@Mapper
public interface SplitExpenseMapper {

    @Mapping(target="name", source="name")
    Friend friendDtoToFriend(FriendDTO friendDTO);

    @Mapping(target="name", source="name")
    FriendDTO newFriendDTOToFriendDTO(NewFriendDTO newFriendDTO);

    @Mappings({
            @Mapping(target = "name", source = "friend.name"),
            @Mapping(target = "balance", source = "balance")
    }    )
    FriendDTO friendBalanceToFriendDTO(Friend friend, double balance);

    @Mappings({
        @Mapping(target="friend.friendId", source="id"),
        @Mapping(target="amount", source="paymentDTO.amount"),
        @Mapping(target="description", source="paymentDTO.description"),
        @Mapping(target="paymentDate", source="now"),
    })
    Payment paymentDtoToPayment(PaymentDTO paymentDTO, Date now, Integer id);

    @Mappings({
            @Mapping(target="name", source="friend.name"),
            @Mapping(target="amount", source="amount"),
            @Mapping(target="description", source="description"),
            @Mapping(target="paymentDate", source="paymentDate"),
    })
    PaymentDTO paymentToPaymentDto(Payment payment);


    @Mappings({
            @Mapping(target="name", source="name"),
            @Mapping(target="amount", source="newPaymentDTO.amount"),
            @Mapping(target="description", source="newPaymentDTO.description"),
    })
    PaymentDTO newPaymentDTOToPaymentDTO(String name, NewPaymentDTO newPaymentDTO);


    @Mappings({
            @Mapping(target="nameFrom", source="nameFrom"),
            @Mapping(target="amount", source="amount"),
            @Mapping(target="nameTo", source="nameTo"),
    })
    CashFlowDTO namesAmountToCashFlowDTO(String nameFrom, String nameTo, double amount);
}
