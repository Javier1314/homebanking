package com.minghub.homebanking;

import com.minghub.homebanking.models.Loan;
import com.minghub.homebanking.repositories.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import static java.util.Optional.empty;
import static java.util.function.Predicate.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.data.repository.util.ClassUtils.hasProperty;

public class RepositoriesTest {

    @DataJpaTest
    @AutoConfigureTestDatabase(replace = NONE)
    public class RepositoriesTest {

        @Autowired
        LoanRepository loanRepository;

        @Test
        public void existLoans(){
            List<Loan> loans = loanRepository.findAll();
            assertThat(loans,is(not(empty())));
        }

        @Test
        public void existPersonalLoan(){
            List<Loan> loans = loanRepository.findAll();
            assertThat(loans, hasItem(hasProperty("name", is("Personal"))));
        }



    }

}
