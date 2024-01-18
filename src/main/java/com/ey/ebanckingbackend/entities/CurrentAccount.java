package com.ey.ebanckingbackend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentAccount extends BankAccount {

    private double overDraft;
}
