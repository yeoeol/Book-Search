package com.example.booksearch.infrastructure.naver.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class NaverBookResponse {

    private List<Item> items;

    @Getter
    @NoArgsConstructor
    @ToString
    static class Item {
        private String title;
        private String author;
        private String isbn;
        private String image; // 이미지 URL
        private String description; // 이미지 URL
    }
}
