package com.example.booksearch.domain;

import jakarta.persistence.*;
import lombok.*;

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

    private String isbn;

    private String title;

    private String author;

    private String publisher;

    private Integer price;

    private String description;

    private String publishedDate;

    private String imageUrl;

}
