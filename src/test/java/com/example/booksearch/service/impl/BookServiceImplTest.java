package com.example.booksearch.service.impl;

import com.example.booksearch.domain.BookDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookServiceImplTest {

    @Autowired
    BookServiceImpl bookService;

    @Test
    void test() {
        String keyword = "해리";
        SearchHits<BookDocument> searchHits =
                bookService.search2(keyword, PageRequest.of(0, 5));

        System.out.println("searchHits = " + searchHits);
    }

}