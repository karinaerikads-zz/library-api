package com.library.libraryapi.api.resouce;

import com.library.libraryapi.api.dto.LoanDTO;
import com.library.libraryapi.api.dto.ReturnedLoanDto;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.entity.Loan;
import com.library.libraryapi.service.BookService;
import com.library.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO){
        Book book = bookService.getBookByISBN(loanDTO.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan loan = Loan.builder()
                .book(book)
                .customer(loanDTO.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        loan = loanService.save(loan);
        return loan.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDto returnedLoanDto){
        Loan loan = loanService.getById(id).get();
        loan.setReturned(returnedLoanDto.getReturned());
        loanService.update(loan);
    }
}
