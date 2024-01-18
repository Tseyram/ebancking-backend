package com.ey.ebanckingbackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransfertRequestDTO {
    private String accountSource;
    private String accountDestination;
    private Double amount;
    private String description;

}
