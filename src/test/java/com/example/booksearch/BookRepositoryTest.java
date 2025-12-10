package com.example.booksearch;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import com.example.booksearch.repository.BulkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BulkRepository bulkRepository;

    private static final int n = 10;

    public Book createBookWithId(Long id) {
        return Book.builder()
                .id(id)
                .isbn("test"+id)
                .title("test"+id)
                .author("test"+id)
                .publisher("test"+id)
                .price(10000)
                .description("test"+id)
                .imageUrl("test"+id)
                .build();
    }

    public Book createBook(Long id) {
        return Book.builder()
                .isbn("test"+id)
                .title("test"+id)
                .author("test"+id)
                .publisher("test"+id)
                .price(10000)
                .description("test"+id)
                .imageUrl("test"+id)
                .build();
    }

    @Test
    void save() {
        Book book = createBook(1L);
        bookRepository.save(book);
    }

    @Test
    void JPA_saveAll() {
        List<Book> books = new ArrayList<>();
        long id = 1;

        for (int i = 0; i < n; i++) {
            books.clear();
            for (int j = 0; j < 1000; j++) {
                books.add(createBook(id++));
            }
            bookRepository.saveAll(books);
        }
    }

    @Test
    void JDBC_bulkInsert() {
        List<Book> books = new ArrayList<>();
        long id = 1;

        for (int i = 0; i < n; i++) {
            books.clear();
            for (int j = 0; j < 1000; j++) {
                books.add(createBook(id++));
            }
            bulkRepository.bulkInsert("books", books);
        }
    }
}