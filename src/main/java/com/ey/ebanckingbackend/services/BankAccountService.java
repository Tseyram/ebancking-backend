package com.ey.ebanckingbackend.services;

import java.util.List;

import com.ey.ebanckingbackend.dtos.AccountHistoryDTO;
import com.ey.ebanckingbackend.dtos.AccountOperationDTO;
import com.ey.ebanckingbackend.dtos.BankAccountDTO;
import com.ey.ebanckingbackend.dtos.CurrentAccountDTO;
import com.ey.ebanckingbackend.dtos.CustomerDTO;
import com.ey.ebanckingbackend.dtos.SavingAccountDTO;
import com.ey.ebanckingbackend.exceptions.BalanceNotSufficientException;
import com.ey.ebanckingbackend.exceptions.BankAccountNotFoundException;
import com.ey.ebanckingbackend.exceptions.CustomerNotFoundException;

public interface BankAccountService {
        CustomerDTO saveCustomer(CustomerDTO customer);

        CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
                        throws CustomerNotFoundException;

        SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
                        throws CustomerNotFoundException;

        List<CustomerDTO> listCustomers();

        BankAccountDTO getBankAccount(String accountId)
                        throws BankAccountNotFoundException;

        void debit(String accountId, double amount, String description)
                        throws BalanceNotSufficientException, BankAccountNotFoundException;

        void credit(String accountId, double amount, String description)
                        throws BankAccountNotFoundException;

        void transfert(String accountIdSource, String accountIdDestination, double amount)
                        throws BankAccountNotFoundException, BalanceNotSufficientException;

        CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

        CustomerDTO updateCustomer(CustomerDTO customerDTO);

        void deleteCustomer(Long customerId);

        List<BankAccountDTO> bankAccountList();

        List<AccountOperationDTO> accountHistory(String accountId);

        AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

        List<CustomerDTO> searchCustomers(String keyword);

}
