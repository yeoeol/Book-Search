package com.example.booksearch.domain;

import com.example.booksearch.application.dto.BookDto;
import jakarta.persistence.*;
import lombok.*;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "books")
@ToString
public class Book {

    @Id
    @Setter
    private Long id;

    @Column(length = 255)
    private String isbn;

    @Column(length = 500)
    private String title;

    @Column(length = 1000)
    private String author;

    @Column(length = 255)
    private String publisher;

    @Column(columnDefinition = "int")
    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String publishedDate;

    @Column(length = 255)
    private String imageUrl;


    public void fillEmptyFields(BookDto dto) {
        if (hasText(dto.getIsbn())) this.isbn = dto.getIsbn();
        if (hasText(dto.getTitle())) this.title = dto.getTitle();
        if (hasText(dto.getAuthor())) this.author = dto.getAuthor();
        if (hasText(dto.getDescription())) this.description = dto.getDescription();
        if (hasText(dto.getImageUrl())) this.imageUrl = dto.getImageUrl();
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
}
