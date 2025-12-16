package com.example.booksearch.domain;

import com.example.booksearch.dto.BookDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Document(indexName = "books", createIndex = true)
@Setting(settingPath = "elasticsearch/book-setting.json")
@Mapping(mappingPath = "elasticsearch/book-mapping.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BookDocument {

    @Id
    @Setter
    @Field(type = FieldType.Text)
    private String id;

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

}
