package com.example.booksearch.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum BookExcelColumn {
    ISBN(Arrays.asList("ISBN", "isbn", "ISBN13", "표준번호", "국제표준도서번호", "책고유번호(ISBN)", "도서번호(ISBN)")),
    TITLE(Arrays.asList("서명", "도서명", "책제목", "제목", "자료명", "Title", "TITLE")),
    AUTHOR(Arrays.asList("저자", "지은이", "글쓴이", "저자명", "저작자", "Author")),
    PUBLISHER(Arrays.asList("출판사", "발행처", "발행자", "Publisher")),
    PRICE(Arrays.asList("정가", "가격", "Price")),
    PUBLISHED_DATE(Arrays.asList("출판년", "발행년", "출판년도", "발행년도", "publishedDate"));

    private final List<String> keywords;

    public boolean isMatch(String cellValue) {
        if (cellValue == null) return false;
        String cleanValue = cellValue.trim().replace(" ", "");
        return keywords.stream()
                .anyMatch(k -> cleanValue.equalsIgnoreCase(k.replace(" ", "")));
    }
}
