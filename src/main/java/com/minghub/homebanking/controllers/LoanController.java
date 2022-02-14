package com.minghub.homebanking.controllers;

import com.minghub.homebanking.dtos.LoanApplicationDTO;
import com.minghub.homebanking.dtos.LoanDTO;
import com.minghub.homebanking.models.*;
import com.minghub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    public AccountRepository accountRepository;

    @Autowired
    public ClientRepository clientRepository;

    @Autowired
    public LoanRepository loanRepository;

    @Autowired
    public ClientLoanRepository clientLoanRepository;

    @Autowired
    public TransactionRepository transactionRepository;

    @RequestMapping(value = "/loans", method = RequestMethod.GET)
    public List<LoanDTO> getLoans(Authentication authentication) {
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }

    @Transactional
    @RequestMapping(value = "/loans", method = RequestMethod.POST)
    public ResponseEntity<Object> applyLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplication) {

        // VALIDACION DATOS
        if (loanApplication.getLoanId() == null || loanApplication.getLoanId() <= 0)
            return new ResponseEntity<>("Invalid LoanId", HttpStatus.FORBIDDEN);
        if (loanApplication.getPayments() <= 0)
            return new ResponseEntity<>("Invalid payment number", HttpStatus.FORBIDDEN);
        if (loanApplication.getAmount() == null || loanApplication.getAmount() <= 0)
            return new ResponseEntity<>("Invalid loan amount ", HttpStatus.FORBIDDEN);
        if (loanApplication.getToAccountNumber() == null || loanApplication.getToAccountNumber() == "")
            return new ResponseEntity<>("Invalid account number ", HttpStatus.FORBIDDEN);


        // VERIFICACION PRESTAMO
        Loan loan = loanRepository.findById(loanApplication.getLoanId()).orElse(null);
        if (loan == null)
            return new ResponseEntity<>("Invalid Loan", HttpStatus.FORBIDDEN);

        // VERIFICACION MONTO
        if (loanApplication.getAmount() > loan.getMaxAmount())
            return new ResponseEntity<>("Invalid amount, provided amount exceeds the maximum", HttpStatus.FORBIDDEN);

        //VERIFICACION DISPONIBILIDAD DE CUOTAS
        boolean found = false;
        for (int payment : loan.getPayments()) {
            if (payment == loanApplication.getPayments()) {
                found = true;
                break;
            }
        }
        if (!found) {
            return new ResponseEntity<>("Invalid payment options, the selected payments are not available for this loan", HttpStatus.FORBIDDEN);
        }

        //VERIFICAION CUENTA DESTINO EXISTA
        Account destAccount = accountRepository.findByNumber(loanApplication.getToAccountNumber());
        if (destAccount == null)
            return new ResponseEntity<>("Invalid destination account", HttpStatus.FORBIDDEN);

        //VERIFICAION PERTENENCIA DE CUENTA DESTION
        Client client = clientRepository.findByEmail(authentication.getName());
        if (destAccount.getClient() != client) {
            return new ResponseEntity<>("Invalid Owner, you're not the owner of the destination account", HttpStatus.FORBIDDEN);
        }



        //SOLICITUD  DE PRESTAMO CON 20% ADICIONAL
        ClientLoan clientLoan = new ClientLoan(client, loan, loanApplication.getAmount() * 1.2, loanApplication.getPayments());
        clientLoanRepository.save(clientLoan);

        //TRANSACICION CREDIT EN CUENTA DESTINO, CON NOMBRE PRESTAMO MAS PRESTAMO APROVADO
        Transaction transaction = new Transaction(TransactionType.CREDIT,loanApplication.getAmount(),loan.getName()+": loan approved", LocalDateTime.now(), destAccount);
        transactionRepository.save(transaction);

        // ACTUALIZAR SALDO CUENTA
        destAccount.setBalance(destAccount.getBalance() + loanApplication.getAmount());


        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

}