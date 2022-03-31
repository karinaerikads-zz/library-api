package com.library.libraryapi.model.repository;

import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static com.library.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Loan createAndPersistLoan() {
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Karina").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        return loan;
    }

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
    public void existsByBookAndNotReturnedTest(){

        Loan loan = createAndPersistLoan();

        boolean exists = loanRepository.existsByBookAndReturned(loan.getBook());

        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo ISBN do livro ou customer")
    public void findByLoanIsbnOrCustomerTest(){
        Loan loan = createAndPersistLoan();

        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer("123", "Karina", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

}
