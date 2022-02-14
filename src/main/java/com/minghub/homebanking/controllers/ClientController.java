package com.minghub.homebanking.controllers;

import com.minghub.homebanking.dtos.ClientDTO;
import com.minghub.homebanking.models.Account;
import com.minghub.homebanking.models.Client;
import com.minghub.homebanking.repositories.AccountRepository;
import com.minghub.homebanking.repositories.ClientRepository;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("/clients")
    public List<ClientDTO> getClients(){
        return this.clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return this.clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    //Mostrar los clientes que estes usando el sistema
    @RequestMapping("/clients/current")
    public ClientDTO getClients(Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return new ClientDTO(client);

    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> createClient(@RequestParam String firstName, @RequestParam String lastName,
                                               @RequestParam String email, @RequestParam String password) {


        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (clientRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
        String accountNumber = "VIN"+(int)((Math.random() * (100000000 - 1))+1);
        accountRepository.save(new Account(accountNumber, LocalDateTime.now(),0,client));
        return new ResponseEntity<>(HttpStatus.CREATED);

    }


}
