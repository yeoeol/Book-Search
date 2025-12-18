package com.example.booksearch.infrastructure.naver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NaverBookSearchServiceTest {

    @Autowired
    NaverBookSearchService naverBookSearchService;

    @Test
    void test() {
        String body = naverBookSearchService.getBookResponse("006028188X", "On the same day in March");
        System.out.println(body);
    }

}