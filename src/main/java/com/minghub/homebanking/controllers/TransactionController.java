package com.minghub.homebanking.controllers;

import com.minghub.homebanking.models.Account;
import com.minghub.homebanking.models.Client;
import com.minghub.homebanking.models.Transaction;
import com.minghub.homebanking.models.TransactionType;
import com.minghub.homebanking.repositories.AccountRepository;
import com.minghub.homebanking.repositories.ClientRepository;
import com.minghub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    public ClientRepository clientRepository;

    @Autowired
    public AccountRepository accountRepository;

    @Autowired
    public TransactionRepository transactionRepository;



    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> doTransaction(@RequestParam String fromAccountNumber,
                                                @RequestParam String toAccountNumber,
                                                @RequestParam String description,
                                                @RequestParam Long amount,
                                                Authentication authentication){

        // VERIFICACION DE PARAMETROS VACIOS
        if(fromAccountNumber == null)
            return new ResponseEntity<>("Invalid transaction source account!",HttpStatus.FORBIDDEN);
        if(toAccountNumber == null)
            return new ResponseEntity<>("Invalid transaction destination account!",HttpStatus.FORBIDDEN);
        if(description == null)
            return new ResponseEntity<>("Invalid transaction description!",HttpStatus.FORBIDDEN);
        if(amount == null || amount <= 0)
            return new ResponseEntity<>("Invalid transaction amount!",HttpStatus.FORBIDDEN);


        //VERIFICACION DE EXISTENCIA DE CUENTAS
        if(accountRepository.findByNumber(fromAccountNumber) == null)
            return new ResponseEntity<>("Source account does not exist!",HttpStatus.FORBIDDEN);
        if(accountRepository.findByNumber(toAccountNumber) == null)
            return new ResponseEntity<>("Destination account does not exist!",HttpStatus.FORBIDDEN);

        //COMPARACION DE IGUALDAD DE CUANTAS DE ORIGEN Y DESTINO
        if(fromAccountNumber.equals(toAccountNumber))
            return new ResponseEntity<>("Source and destination accounts are the same!",HttpStatus.FORBIDDEN);

        //VERIFICACION DE PERTENENCIA DE LA CUENTA AL USUARIO
        Client client = clientRepository.findByEmail(authentication.getName());
        Set<Account> accounts = client.getAccounts();
        Iterator iter = accounts.iterator();
        Boolean found = false;
        Account srcAccount = null;
        while(iter.hasNext()){
            Account account = (Account) iter.next();
            if(account.getNumber().equals(fromAccountNumber)) {
                found = true;

                //CUENTA DE ORIGEN
                srcAccount = account;
                break;
            }
        }
        if(!found)
            return new ResponseEntity<>("Authenticated user does not own the source account!",HttpStatus.FORBIDDEN);

        //VERIFICAION DE SALDO
        if(srcAccount.getBalance() < amount)
            return new ResponseEntity<>("Insufficient funds, ilegal operation!",HttpStatus.FORBIDDEN);

        //REALIZAR LA TRANSACCION
        Account destAccount = accountRepository.findByNumber(toAccountNumber);

        Transaction srcTransaction = new Transaction(TransactionType.DEBIT, -amount, description, LocalDateTime.now(), srcAccount);
        Transaction destTransaction = new Transaction(TransactionType.CREDIT,amount, description, LocalDateTime.now(), destAccount);
        transactionRepository.save(srcTransaction);
        transactionRepository.save(destTransaction);

        srcAccount.setBalance(srcAccount.getBalance() - amount);
        destAccount.setBalance(destAccount.getBalance() + amount);
        return new ResponseEntity<>("Operation completed successfully", HttpStatus.CREATED);
    }
}