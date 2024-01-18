package com.ey.ebanckingbackend.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ey.ebanckingbackend.dtos.AccountHistoryDTO;
import com.ey.ebanckingbackend.dtos.AccountOperationDTO;
import com.ey.ebanckingbackend.dtos.BankAccountDTO;
import com.ey.ebanckingbackend.dtos.CurrentAccountDTO;
import com.ey.ebanckingbackend.dtos.CustomerDTO;
import com.ey.ebanckingbackend.dtos.SavingAccountDTO;
import com.ey.ebanckingbackend.entities.AccountOperation;
import com.ey.ebanckingbackend.entities.BankAccount;
import com.ey.ebanckingbackend.entities.CurrentAccount;
import com.ey.ebanckingbackend.entities.Customer;
import com.ey.ebanckingbackend.entities.SavingAccount;
import com.ey.ebanckingbackend.enums.OperationType;
import com.ey.ebanckingbackend.exceptions.BalanceNotSufficientException;
import com.ey.ebanckingbackend.exceptions.BankAccountNotFoundException;
import com.ey.ebanckingbackend.exceptions.CustomerNotFoundException;
import com.ey.ebanckingbackend.mappers.BankAccountMapper;
import com.ey.ebanckingbackend.repositories.AccountOperationRepository;
import com.ey.ebanckingbackend.repositories.BankAccountRepository;
import com.ey.ebanckingbackend.repositories.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapper dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("saving new Customer");
        Customer customer = customerRepository.save(dtoMapper.fromCustomerDTO(customerDTO));
        return dtoMapper.fromCustomer(customer);

    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentAccount bankAccount = new CurrentAccount();

        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setOverDraft(overDraft);
        return dtoMapper.fromCurrentAccount(bankAccountRepository.save(bankAccount));
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        SavingAccount bankAccount = new SavingAccount();

        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setInterestRate(interestRate);
        return dtoMapper.fromSavingAccount(bankAccountRepository.save(bankAccount));

    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(dtoMapper::fromCustomer)
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount instanceof SavingAccount) {
            return dtoMapper.fromSavingAccount((SavingAccount) bankAccount);
        } else {
            return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
        }

    }

    @Override
    public void debit(String accountId, double amount, String description)
            throws BalanceNotSufficientException, BankAccountNotFoundException {
        BankAccount bankAccountToDebit = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccountToDebit.getBalance() < amount) {
            throw new BalanceNotSufficientException("Balance not sufficient");
        }
        AccountOperation debit = new AccountOperation();
        debit.setAmout(amount);
        debit.setBankAccount(bankAccountToDebit);
        debit.setOperationDate(new Date());
        debit.setDescription(description);
        debit.setType(OperationType.DEBIT);
        accountOperationRepository.save(debit);
        bankAccountToDebit.setBalance(bankAccountToDebit.getBalance() - amount);
        bankAccountRepository.save(bankAccountToDebit);

    }

    @Override
    public void credit(String accountId, double amount, String description)
            throws BankAccountNotFoundException {
        BankAccount bankAccountToCredit = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));

        AccountOperation credit = new AccountOperation();
        credit.setAmout(amount);
        credit.setBankAccount(bankAccountToCredit);
        credit.setOperationDate(new Date());
        credit.setDescription(description);
        credit.setType(OperationType.CREDIT);
        accountOperationRepository.save(credit);
        bankAccountToCredit.setBalance(bankAccountToCredit.getBalance() + amount);
        bankAccountRepository.save(bankAccountToCredit);
    }

    @Override
    public void transfert(String accountIdSource, String accountIdDestination, double amount)
            throws BalanceNotSufficientException, BankAccountNotFoundException {
        debit(accountIdSource, amount, "transfert de ");
        credit(accountIdDestination, amount, "transfer vers");

    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer customer = customerRepository.save(dtoMapper.fromCustomerDTO(customerDTO));
        return dtoMapper.fromCustomer(customer);

    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        return bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                return dtoMapper.fromSavingAccount((SavingAccount) bankAccount);
            } else {
                return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(dtoMapper::fromAccountOperation).collect(Collectors.toList());

    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));
        Page<AccountOperation> accountOperations = accountOperationRepository
                .findByBankAccountIdOrderByOperationDateDesc(accountId,
                        PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationsDTOs = accountOperations.getContent().stream()
                .map(dtoMapper::fromAccountOperation).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOs(accountOperationsDTOs);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());

        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {

        return customerRepository.searchCustomers(keyword).stream().map(dtoMapper::fromCustomer)
                .collect(Collectors.toList());
    }
}