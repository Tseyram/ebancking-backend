package com.ey.ebanckingbackend.dtos;

import java.util.Date;

import com.ey.ebanckingbackend.enums.OperationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;

    private OperationType type;

    private String description;
}
