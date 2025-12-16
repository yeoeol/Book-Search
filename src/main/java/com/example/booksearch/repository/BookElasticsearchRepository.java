package com.example.booksearch.repository;

import com.example.booksearch.domain.BookDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookElasticsearchRepository extends ElasticsearchRepository<BookDocument, Long> {
}
