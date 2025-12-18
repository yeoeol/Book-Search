package com.example.booksearch.domain.repository;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BulkRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ElasticsearchOperations elasticsearchOperations;

    private static final int BATCH_SIZE = 1000;

    @Transactional
    public void bulkInsert(String table, List<Book> books) {
        String updateSql = "UPDATE sequences " +
                           "SET next_val = LAST_INSERT_ID(next_val + ?) " +
                           "WHERE name = ?";
        jdbcTemplate.update(updateSql, books.size(), table);

        Long lastId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        String sql = "INSERT INTO books (id, isbn, title, author, publisher, price, description, published_date, image_url) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        long currentId = lastId - books.size();
        for (Book book : books) {
            book.setId(currentId++);
        }

        jdbcTemplate.batchUpdate(sql,
                books,
                BATCH_SIZE,
                (PreparedStatement ps, Book book) -> {
                    ps.setLong(1, book.getId());
                    ps.setString(2, book.getIsbn());
                    ps.setString(3, book.getTitle());
                    ps.setString(4, book.getAuthor());
                    ps.setString(5, book.getPublisher());
                    ps.setInt(6, book.getPrice());
                    ps.setString(7, book.getDescription());
                    ps.setString(8, book.getPublishedDate());
                    ps.setString(9, book.getImageUrl());
                }
        );
        log.info("{} ~ {}", books.get(0).getId(), books.get(books.size()-1).getId());
    }

    public void bulkInsertForElasticsearch(String index, List<BookDocument> documents) {
        List<IndexQuery> queries = documents.stream()
                .map(document -> new IndexQueryBuilder()
                        .withObject(document)
                        .build())
                .collect(Collectors.toList());

        elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(index));
    }
}
