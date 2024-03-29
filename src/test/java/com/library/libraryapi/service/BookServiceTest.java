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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        Book book = createValidBook();
        when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        when(bookRepository.save(book)).thenReturn(
                Book.builder().id(1l)
                        .isbn("1234")
                        .author("Fulano")
                        .title("As Aventuras")
                        .build()
        );

        Book savedBook = bookService.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("1234");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
        assertThat(savedBook.getTitle()).isEqualTo("As Aventuras");
    }

    @Test
    @DisplayName("Deve lançar um erro de negócio ao tentar salvar um livro com ISBN duplicado")
    public void sholdNotSaveABookWithDuplicatedISBN(){
        Book book = createValidBook();
        when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception =  Assertions.catchThrowable(() -> bookService.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já cadastrado.");

        Mockito.verify(bookRepository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTeste(){
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());

    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id quando ele não existe na base")
    public void bookNotFoundByIdTest(){
        Long id = 1l;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> book = bookService.getById(id);

        assertThat(book.isPresent()).isFalse();

    }

    private Book createValidBook() {
        return Book.builder().isbn("1234").author("Fulano").title("As Aventuras").build();
    }

    @Test
    @DisplayName("Deleta um livro com sucesso")
    public void deleteBookTest(){
        Book book = Book.builder().id(1l).build();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(book));

        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Falha ao deletar livro")
    public void deleteInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));

        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Falha ao atualizar livro")
    public void updateInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Sucesso ao atualizar livro")
    public void updateBookTeste(){
        long id = 1l;
        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = createValidBook();
        updatedBook.setId(id);
        when(bookRepository.save(updatingBook)).thenReturn(updatedBook);

        Book book = bookService.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades com sucesso")
    public void findBookTest(){
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> bookList = Arrays.asList(book);
        Page<Book> page =  new PageImpl<Book>(bookList, pageRequest, 1);
        when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> result = bookService.find(book, pageRequest);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(bookList);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro pelo ISBN")
    public void getBookByIsbnTest(){

        String isbn = "1230";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1l).isbn(isbn).build()));

        Optional<Book> book = bookService.getBookByISBN(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1l);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(bookRepository, times(1)).findByIsbn(isbn);
    }

}
