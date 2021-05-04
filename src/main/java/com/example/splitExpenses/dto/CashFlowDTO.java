package com.example.splitExpenses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashFlowDTO {
    private String nameFrom;
    private String nameTo;
    private double amount;

}
