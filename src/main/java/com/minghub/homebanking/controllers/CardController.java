package com.minghub.homebanking.controllers;

import com.minghub.homebanking.dtos.CardDTO;
import com.minghub.homebanking.models.Card;
import com.minghub.homebanking.models.CardColor;
import com.minghub.homebanking.models.CardType;
import com.minghub.homebanking.models.Client;
import com.minghub.homebanking.repositories.CardRepository;
import com.minghub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/clients/currents/cards")
    public List<CardDTO> getCards (org.springframework.security.core.Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return client.getCards().stream().map(CardDTO::new).collect(Collectors.toList());
    }

    @PostMapping("/clients/current/cards")
    private ResponseEntity<Object> createCard(@RequestParam CardType cardType, CardColor cardColor, Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());

        if(client.getCards().stream().filter(card -> card.getType() == cardType).count() >=3){
            return new ResponseEntity<>("Has superado el limite de tarjetas"+ cardType+ ".-", HttpStatus.FORBIDDEN);

        }else{
            String cardNumber =
                    (int)((Math.random() * (9999-1000)) + 1000)+"-"
                            +(int)((Math.random() * (9999-1000)) + 1000)+"-"
                            +(int)((Math.random() * (9999-1000)) + 1000)+"-"
                            +(int)((Math.random() * (9999-1000)) + 1000);
            int cvv = (int)((Math.random() * (999-100)) + 100);


            cardRepository.save(new Card(client.getFirstName()+" "+client.getLastName(),cardType,cardColor,cardNumber,cvv, LocalDateTime.now(), LocalDateTime.now().plusYears(5),client));

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}
