package com.example.booksearch.repository;

import com.example.booksearch.domain.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookElasticsearchRepository extends ElasticsearchRepository<BookDocument, String> {
    Page<BookDocument> findByTitleMatches(String keyword, Pageable pageable);
}
