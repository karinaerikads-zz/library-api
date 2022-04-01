package com.library.libraryapi.api.resouce;

import com.library.libraryapi.api.dto.BookDTO;
import com.library.libraryapi.api.dto.LoanDTO;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.entity.Loan;
import com.library.libraryapi.service.BookService;
import com.library.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
@Slf4j
public class BookController {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a book")
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO){
        log.info("Creating a book for isbn: {}", bookDTO.getIsbn());
        Book book = modelMapper.map(bookDTO, Book.class);
        book = bookService.save(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping("{id}")
    @ApiOperation("Obtains a book details by id")
    public BookDTO get(@PathVariable Long id){
        log.info("Obtaining details for book id: {}", id);
        return bookService
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book by id")
    @ApiResponses({@ApiResponse(code = 204, message = "Book succesfully deleted")})
    public void delete(@PathVariable Long id){
        log.info("Deleting book of id: {}", id);
        Book book = bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @PutMapping("{id}")
    @ApiOperation("Updates a book by id")
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO bookDTO){
        log.info("Updating book of id: {}", id);
        return bookService.getById(id).map( book -> {
            book.setAuthor(bookDTO.getAuthor());
            book.setTitle(bookDTO.getTitle());
            book = bookService.update(book);

        return modelMapper.map(book, BookDTO.class);

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Find books by params")
    public Page<BookDTO> find (BookDTO bookDTO, Pageable pageRequest){
        Book book = modelMapper.map(bookDTO, Book.class);
        Page<Book> result = bookService.find(book, pageRequest);
        List<BookDTO> BookDTOList = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(BookDTOList, pageRequest, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    @ApiOperation("Find loans by params")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable){
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDTO> listLoanDTO = result.getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBookDTO(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(listLoanDTO, pageable, result.getTotalElements());
    }
}
