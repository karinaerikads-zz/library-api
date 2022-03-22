package com.library.libraryapi.model.repository;

import com.library.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um  livro com isbn cadastrado")
    public void returnTrueWhenIsbnExists(){
        String isbn = "1234";
        Book book = createNewBook();
        entityManager.persist(book);

        boolean exist = bookRepository.existsByIsbn(isbn);

        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um  livro com isbn cadastrado")
    public void returnFalseWhenIsbnDoesntExists(){
        String isbn = "1234";

        boolean exist = bookRepository.existsByIsbn(isbn);

        assertThat(exist).isFalse();

    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void findByIdTeste(){
        Book book = createNewBook();
        entityManager.persist(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());

        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Salvar livro com sucesso")
    public void saveBookTest(){
        Book book = createNewBook();

        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deleta livro com sucesso")
    public void deleteBookTest(){
        Book book = createNewBook();
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());

        bookRepository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();
    }

    private Book createNewBook() {
        return Book.builder()
                .isbn("1234")
                .author("Fulano")
                .title("As Aventuras")
                .build();
    }
}
