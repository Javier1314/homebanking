package com.minghub.homebanking.repositories;

import com.minghub.homebanking.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface AccountRepository extends JpaRepository<Account, Long> {
    public Account findByNumber(String number);

    List<Account> findByNumberContainingIgnoreCaseOrderByNumberAsc(String number);

    Account findAccountByNumber(String fromAccountNumber);
}
