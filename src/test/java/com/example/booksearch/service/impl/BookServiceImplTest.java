package com.example.booksearch.service.impl;

import com.example.booksearch.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

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