package com.library.libraryapi.service;

import com.library.libraryapi.BusinessException;
import com.library.libraryapi.api.dto.LoanFilterDTO;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.entity.Loan;
import com.library.libraryapi.model.repository.LoanRepository;
import com.library.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public static Loan createLoan(){
        Book book = Book.builder().id(1l).build();
        String customer = "Karina";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book).build();
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
        Loan loan = createLoan();

        when(loanRepository.existsByBookAndReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> loanService.save(loan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(loanRepository, never()).save(loan);
    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID")
    public void getLoanDetailsTest(){
        Long id = 1l;
        Loan loan = createLoan();
        loan.setId(1l);

        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        Optional<Loan> result = loanService.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(loanRepository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void updateLoanTest(){
        Loan loan = createLoan();
        loan.setId(1l);
        loan.setReturned(true);

        when(loanRepository.save(loan)).thenReturn(loan);
        Loan updatedLoan = loanService.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar empréstimos pelas propriedades com sucesso")
    public void findLoanTest(){
        Loan loan = createLoan();
        loan.setId(1l);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Loan> loanList = Arrays.asList(loan);
        Page<Loan> page =  new PageImpl<Loan>(loanList, pageRequest, loanList.size());
        when(loanRepository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Karina").isbn("321").build();
        Page<Loan> result = loanService.find(loanFilterDTO, pageRequest);

        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(loanList);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}
