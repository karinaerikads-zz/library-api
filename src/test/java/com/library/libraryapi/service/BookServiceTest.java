package com.library.libraryapi.service;

import com.library.libraryapi.BusinessException;
import com.library.libraryapi.model.entity.Book;
import com.library.libraryapi.model.repository.BookRepository;
import com.library.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp(){
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //Cenário
        Book book = createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(bookRepository.save(book)).thenReturn(
                Book.builder().id(1l)
                        .isbn("1234")
                        .author("Fulano")
                        .title("As Aventuras")
                        .build()
        );

        //Execução
        Book savedBook = bookService.save(book);

        //Verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("1234");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
        assertThat(savedBook.getTitle()).isEqualTo("As Aventuras");
    }

    @Test
    @DisplayName("Deve lançar um erro de negócio ao tentar salvar um livro com ISBN duplicado")
    public void sholdNotSaveABookWithDuplicatedISBN(){
        //Cenário
        Book book = createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //Execução
        Throwable exception =  Assertions.catchThrowable(() -> bookService.save(book));

        //Validações
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já cadastrado.");

        Mockito.verify(bookRepository, Mockito.never()).save(book);

    }

    private Book createValidBook() {
        return Book.builder().isbn("1234").author("Fulano").title("As Aventuras").build();
    }

}
