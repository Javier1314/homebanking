package com.minghub.homebanking;

import com.minghub.homebanking.models.*;
import com.minghub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;


@SpringBootApplication
public class HomebankingApplication {

	@Autowired
	private PasswordEncoder passwordEnconder;

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (arg) -> {
			Client client1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEnconder.encode("123456"));
			Client client2 = new Client("Javier", "Mella", "javiermellap@gmail.com", passwordEnconder.encode("123456"));
			Client client3 = new Client("Luna","Fuentes","fluna@hotmail.cl", passwordEnconder.encode("123456"));
			clientRepository.save(client1);
			clientRepository.save(client2);
			clientRepository.save(client3);
			Account account1 = new Account("VIN001", LocalDateTime.now(), 500000, client1);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500, client1);
			accountRepository.save(account1);
			accountRepository.save(account2);
			Account account3 = new Account("VIN003", LocalDateTime.now(), 900000, client2 );
			Account account4 = new Account("VIN004", LocalDateTime.now(), 15500, client2 );
			accountRepository.save(account3);
			accountRepository.save(account4);


			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 1500, "Compra Pancito Tia Rosa", LocalDateTime.now(), account1 );
			Transaction transaction2 = new Transaction(TransactionType.CREDIT, 150000, "Compra Lavadora GL", LocalDateTime.now(), account3 );
			Transaction transaction3 = new Transaction(TransactionType.CREDIT, 19500, "Compra WebCam MarketPlace", LocalDateTime.now(), account1 );
			Transaction transaction4 = new Transaction(TransactionType.DEBIT, -18000, "Pago Agua Diciembre", LocalDateTime.now(), account3 );
			Transaction transaction5 = new Transaction(TransactionType.DEBIT, -24000, "Pago  Luz Diciembre", LocalDateTime.now(), account1 );
			Transaction transaction6 = new Transaction(TransactionType.DEBIT, -3000, "Pago saldo MetroVal", LocalDateTime.now(), account3 );
			Transaction transaction7 = new Transaction(TransactionType.CREDIT, 25000, "Compra Ropa FalaFala", LocalDateTime.now(), account1 );
			Transaction transaction8 = new Transaction(TransactionType.CREDIT, 105000, "Compra Silla Gamer ", LocalDateTime.now(), account3 );
			Transaction transaction9 = new Transaction(TransactionType.DEBIT, -15000, "Pago Gastos Comunes ", LocalDateTime.now(), account1 );
			Transaction transaction10 = new Transaction(TransactionType.DEBIT, -9900, "Pago Plan Celular", LocalDateTime.now(), account3 );

			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);
			transactionRepository.save(transaction7);
			transactionRepository.save(transaction8);
			transactionRepository.save(transaction9);
			transactionRepository.save(transaction10);

			Loan loan1 = new Loan("Hipotecario",500000, Arrays.asList(12,24,36,48,60));
			Loan loan2 = new Loan("Personal",100000, Arrays.asList(6,12,24));
			Loan loan3 = new Loan("Automotriz",300000, Arrays.asList(6,12,24,36));
			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			ClientLoan cl1 = new ClientLoan(300000,12,client1,loan1);
			ClientLoan cl2 = new ClientLoan(500002,6,client1,loan2);
			ClientLoan cl3 = new ClientLoan(200000,12,client2,loan3);
			ClientLoan cl4 = new ClientLoan(100000,6,client2,loan2);
			clientLoanRepository.save(cl1);
			clientLoanRepository.save(cl2);
			clientLoanRepository.save(cl3);
			clientLoanRepository.save(cl4);

			Card card1 = new Card(client1.getFirstName()+" "+client1.getLastName(),CardType.DEBIT,CardColor.GOLD,"4199-7265-9785-6577",303,LocalDateTime.now(),LocalDateTime.now().plusYears(5),client1);
			cardRepository.save(card1);

			Card card2 = new Card(client1.getFirstName()+" "+client1.getLastName(),CardType.CREDIT,CardColor.TITANIUM,"4841-1485-3929-5008",115,LocalDateTime.now(),LocalDateTime.now().plusYears(5),client1);
			Card card3 = new Card(client2.getFirstName()+" "+client2.getLastName(),CardType.CREDIT,CardColor.SILVER,"4349-9840-6574-9233",221,LocalDateTime.now(),LocalDateTime.now().plusYears(7),client2);
			cardRepository.save(card2);
			cardRepository.save(card3);





		};
	}
}