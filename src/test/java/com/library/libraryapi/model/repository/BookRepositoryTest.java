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
        //cenário
        String isbn = "1234";
        Book book = createNewBook();
        entityManager.persist(book);

        //execucao
        boolean exist = bookRepository.existsByIsbn(isbn);

        //verificacao
        assertThat(exist).isTrue();
    }

    private Book createNewBook() {
        return Book.builder()
                .isbn("1234")
                .author("Fulano")
                .title("As Aventuras")
                .build();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um  livro com isbn cadastrado")
    public void returnFalseWhenIsbnDoesntExists(){
        //cenário
        String isbn = "1234";

        //execucao
        boolean exist = bookRepository.existsByIsbn(isbn);

        //verificacao
        assertThat(exist).isFalse();

    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void findByIdTeste(){
        //Cenário
        Book book = createNewBook();
        entityManager.persist(book);

        //execução
        Optional<Book> foundBook = bookRepository.findById(book.getId());

        //Verificações
        assertThat(foundBook.isPresent()).isTrue();
    }
}
