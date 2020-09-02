package com.library.libraryapi.api.resouce;

import com.library.libraryapi.api.dto.BookDTO;
import com.library.libraryapi.api.exception.APIErrors;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")

public class BookController {

    private BookService bookService;
    private ModelMapper modelMapper;

    public BookController(BookService bookService, ModelMapper mapper) {
        this.bookService = bookService;
        this.modelMapper = mapper;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Validated BookDTO bookDTO){
        Book book = modelMapper.map(bookDTO, Book.class);
        book = bookService.save(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIErrors handleValidadionExceptions(MethodArgumentNotValidException exception){
        BindingResult bindingResult = exception.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();

        return new APIErrors(bindingResult);
    }
}
