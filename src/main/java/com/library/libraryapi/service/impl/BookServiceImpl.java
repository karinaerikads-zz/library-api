package com.library.libraryapi.service.impl;

import com.library.libraryapi.BusinessException;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.repository.BookRepository;
import com.library.libraryapi.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        if (book == null || book.getId() == null)
            throw new IllegalArgumentException("Id do livro não pode ser nulo");

        this.bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null)
            throw new IllegalArgumentException("Id do livro não pode ser nulo");

        return this.bookRepository.save(book);
    }

    @Override
    public Page<Book> find(Book book, Pageable pageRequest) {
        return null;
    }
}
