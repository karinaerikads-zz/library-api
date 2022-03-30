package com.library.libraryapi.service.impl;

import com.library.libraryapi.BusinessException;
import com.library.libraryapi.model.entity.Loan;
import com.library.libraryapi.model.repository.LoanRepository;
import com.library.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if (loanRepository.existsByBookAndReturned(loan.getBook()))
            throw new BusinessException("Book already loaned");

        return loanRepository.save(loan);
    }
}
