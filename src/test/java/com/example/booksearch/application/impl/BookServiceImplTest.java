package com.example.booksearch.application.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class BookServiceImplTest {

    @Autowired
    BookServiceImpl bookService;

    @Test
    void test() {
        String keyword = "해리 포터";

        bookService.search(keyword, PageRequest.of(0, 5));

//        System.out.println(search);
    }

}