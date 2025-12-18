package com.example.booksearch.application.dto;

import com.example.booksearch.domain.Book;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.util.List;

@Getter
@ToString
@JacksonXmlRootElement(localName = "root")
public class BookApiResponse {

    @JacksonXmlProperty(localName = "paramData")
    private ParamData paramData;

    @JacksonXmlProperty(localName = "result")
    private Result result;

    @Getter
    @ToString
    public static class ParamData {
        private String kwd;
        private String category;
        private int pageNum;
        private int pageSize;
        private int total;
    }

    @Getter
    @ToString
    public static class Result {
        @JacksonXmlProperty(localName = "item")                 // 각 요소는 <item> 태그임
        @JacksonXmlElementWrapper(useWrapping = false)        // 내부 리스트 요소 <item> 처리
        private List<Item> items;
    }

    @Getter
    @ToString
    public static class Item {
        @JacksonXmlProperty(localName = "isbn")
        private String isbn;
        @JacksonXmlProperty(localName = "title_info")
        private String titleInfo;
        @JacksonXmlProperty(localName = "author_info")
        private String authorInfo;
        @JacksonXmlProperty(localName = "pub_info")
        private String pubInfo;
        @JacksonXmlProperty(localName = "pub_year_info")
        private String pubYearInfo;

        public Book toBook() {
            return Book.builder()
                    .isbn(isbn)
                    .title(titleInfo)
                    .author(authorInfo)
                    .publisher(pubInfo)
                    .publishedDate(pubYearInfo)
                    .build();
        }
    }
}
