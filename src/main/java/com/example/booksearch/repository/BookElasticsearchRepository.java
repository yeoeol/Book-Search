package com.example.booksearch.repository;

import com.example.booksearch.domain.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookElasticsearchRepository extends ElasticsearchRepository<BookDocument, String> {
    Page<BookDocument> findByTitleContaining(String keyword, Pageable pageable);

//    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"author\", \"publisher\", \"description\"]}}")
//    Page<BookDocument> findByKeyword(String keyword, Pageable pageable);
}
