package com.library.libraryapi.service;

import com.library.libraryapi.api.dto.LoanFilterDTO;
import com.library.libraryapi.api.resouce.BookController;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO loanFilterDTO, Pageable pageable);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();
}
