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

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String publishedDate;

    @Column(length = 255)
    private String imageUrl;

}
