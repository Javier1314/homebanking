package com.minghub.homebanking.controllers;

import com.minghub.homebanking.dtos.AccountDTO;
import com.minghub.homebanking.models.Account;
import com.minghub.homebanking.models.Client;
import com.minghub.homebanking.repositories.AccountRepository;
import com.minghub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {



    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public List<AccountDTO> getClients(){
        return this.accountRepository.findAll().stream().map(AccountDTO::new).collect(toList());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id, Authentication authentication){

        Client client = this.clientRepository.findByEmail(authentication.getName());
        Set<Account> accounts = client.getAccounts();

        Iterator iter = accounts.iterator();
        while (iter.hasNext()){
            Account account = (Account) iter.next();
            if (account.getId() == id){
                return this.accountRepository.findById(id).map(AccountDTO::new).orElse(null);
            }
        }
        return null;
    }

    @PostMapping("/clients/current/accounts")
    private ResponseEntity<Object> createAccount(Authentication authentication){

        Client client = this.clientRepository.findByEmail(authentication.getName());

        if(client.getAccounts().size()>=3){
            return new ResponseEntity<>("Clients of accounts limit reached", HttpStatus.FORBIDDEN);

        }else{
            String accountNumber = ("VIN" + (int)(Math.random() * (10000000-1)+1));
            accountRepository.save(new Account(accountNumber, LocalDateTime.now(), 0 ,client));

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @GetMapping("/clients/current/accounts")
    public List<AccountDTO> getAccounts(Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return client.getAccounts().stream().map(AccountDTO::new).collect(toList());
    }




}