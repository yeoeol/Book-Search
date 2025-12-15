package com.example.booksearch.repository;

import com.example.booksearch.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT B " +
            "FROM Book B " +
            "WHERE B.title " +
                "LIKE concat(:title, '%')"
    )
    Page<Book> findByTitleStartsWith(String title, Pageable pageable);
}
