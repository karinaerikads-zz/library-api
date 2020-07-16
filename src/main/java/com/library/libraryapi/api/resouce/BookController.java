package com.library.libraryapi.api.resouce;

import com.library.libraryapi.api.dto.BookDTO;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")

public class BookController {

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    private BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO bookDTO){
        Book book =
                Book.builder()
                .author(bookDTO.getAuthor())
                .title(bookDTO.getTitle())
                .isbn(bookDTO.getIsbn())
                .build();
        book = bookService.save(book);
        return bookDTO.builder()
                .id(book.getId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .build();
    }
}
