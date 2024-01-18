package com.ey.ebanckingbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.ebanckingbackend.entities.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

}
