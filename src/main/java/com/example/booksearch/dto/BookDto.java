package com.example.booksearch.dto;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class BookDto {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer price;
    private String description;
    private String publishedDate;
    private String imageUrl;

    public static BookDto from(Book book) {
        return BookDto.builder()
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

    public static BookDto from(BookDocument bookDocument) {
        return BookDto.builder()
                .isbn(bookDocument.getIsbn())
                .title(bookDocument.getTitle())
                .author(bookDocument.getAuthor())
                .publisher(bookDocument.getPublisher())
                .price(bookDocument.getPrice())
                .description(bookDocument.getDescription())
                .publishedDate(bookDocument.getPublishedDate())
                .imageUrl(bookDocument.getImageUrl())
                .build();
    }

    public Book toBook() {
        return Book.builder()
                .isbn(isbn)
                .title(title)
                .author(author)
                .publisher(publisher)
                .price(price)
                .description(description)
                .publishedDate(publishedDate)
                .imageUrl(imageUrl)
                .build();
    }

    public BookDocument toBookDocument() {
        return BookDocument.builder()
                .isbn(isbn)
                .title(title)
                .author(author)
                .publisher(publisher)
                .price(price)
                .description(description)
                .publishedDate(publishedDate)
                .imageUrl(imageUrl)
                .build();
    }
}
