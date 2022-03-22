package com.library.libraryapi.service.impl;

import com.library.libraryapi.BusinessException;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.repository.BookRepository;
import com.library.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if(bookRepository.existsByIsbn(book.getIsbn()))
            throw new BusinessException("ISBN já cadastrado.");

        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return this.bookRepository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getIsbn() == null)
            throw new IllegalArgumentException("Não é possível deletar pois o ISBN do livro é inválido");

        this.bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {
        return null;
    }
}
