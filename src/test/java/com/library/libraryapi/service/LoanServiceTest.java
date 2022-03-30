package com.library.libraryapi.service;

import com.library.libraryapi.BusinessException;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.entity.Loan;
import com.library.libraryapi.model.repository.LoanRepository;
import com.library.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;


import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService loanService;

    @MockBean
    LoanRepository loanRepository;

    @BeforeEach
    public void setUp(){
        this.loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest() {
        Book book = Book.builder().id(1l).build();
        String customer = "Karina";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book).build();

        Loan loanSaved = Loan.builder()
                .id(1l)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book).build();

        when(loanRepository.existsByBookAndReturned(book)).thenReturn(false);
        when(loanRepository.save(savingLoan)).thenReturn(loanSaved);

        Loan loan = loanService.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(loanSaved.getId());
        assertThat(loan.getBook().getId()).isEqualTo(loanSaved.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(loanSaved.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(loanSaved.getLoanDate());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado")
    public void loanedBookSaveTest() {
        Book book = Book.builder().id(1l).build();
        String customer = "Karina";

        Loan loan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book).build();

        when(loanRepository.existsByBookAndReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> loanService.save(loan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(loanRepository, never()).save(loan);
    }
}
