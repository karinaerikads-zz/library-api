package com.library.libraryapi.service;

import com.library.libraryapi.api.resouce.BookController;
import com.library.libraryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
