package com.ey.ebanckingbackend;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ey.ebanckingbackend.dtos.CustomerDTO;
import com.ey.ebanckingbackend.entities.AccountOperation;
import com.ey.ebanckingbackend.entities.CurrentAccount;
import com.ey.ebanckingbackend.entities.SavingAccount;
import com.ey.ebanckingbackend.enums.AccountStatus;
import com.ey.ebanckingbackend.enums.OperationType;
import com.ey.ebanckingbackend.repositories.AccountOperationRepository;
import com.ey.ebanckingbackend.repositories.BankAccountRepository;
import com.ey.ebanckingbackend.repositories.CustomerRepository;
import com.ey.ebanckingbackend.services.BankAccountService;

@SpringBootApplication
public class EbanckingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbanckingBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner start(CustomerRepository customerRepository,
			BankAccountService bankAccountService,
			BankAccountRepository bankAccountRepository,
			AccountOperationRepository accountOperationRepository) {
		return args -> {
			Stream.of("Os", "Ey", "Kais", "Rosa").forEach(name -> {
				CustomerDTO customer = new CustomerDTO();
				customer.setName(name);
				customer.setEmail(name + "@gmail.com");
				bankAccountService.saveCustomer(customer);
			});

			customerRepository.findAll().forEach(cust -> {
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random() * 9000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(9000);

				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random() * 9000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);

				bankAccountRepository.save(savingAccount);
			});
			bankAccountRepository.findAll().forEach(acc -> {
				for (int i = 0; i < 10; i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmout(Math.random() * 1200);
					accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}
			});

		};

	}
}