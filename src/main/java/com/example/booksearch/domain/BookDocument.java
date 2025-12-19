package com.example.booksearch.domain;

import com.example.booksearch.application.dto.BookDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.util.StringUtils;

@Getter
@Document(indexName = "books", createIndex = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BookDocument {

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String isbn;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String author;

    @Field(type = FieldType.Text)
    private String publisher;

    @Field(type = FieldType.Integer)
    private Integer price;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String publishedDate;

    @Field(type = FieldType.Text)
    private String imageUrl;

    public static BookDocument from(Book book) {
        return BookDocument.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .price(book.getPrice())
                .description(book.getDescription())
                .publishedDate(book.getPublishedDate())
                .imageUrl(book.getImageUrl())
                .build();
    }

    public void updateIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateAuthor(String author) {
        this.author = author;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void fillEmptyFields(BookDto dto) {
        if (StringUtils.hasText(dto.getIsbn())) this.isbn = dto.getIsbn();
        if (StringUtils.hasText(dto.getTitle())) this.title = dto.getTitle();
        if (StringUtils.hasText(dto.getAuthor())) this.author = dto.getAuthor();
        if (StringUtils.hasText(dto.getDescription())) this.description = dto.getDescription();
        if (StringUtils.hasText(dto.getImageUrl())) this.imageUrl = dto.getImageUrl();
    }
}
