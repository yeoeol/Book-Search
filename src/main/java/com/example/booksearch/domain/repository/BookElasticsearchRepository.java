package com.example.booksearch.domain.repository;

import com.example.booksearch.domain.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookElasticsearchRepository extends ElasticsearchRepository<BookDocument, String> {
    Page<BookDocument> findByTitleMatches(String keyword, Pageable pageable);

    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": [
                  "title^3",
                  "author^2",
                  "publisher",
                  "isbn^5"
                ]
              }
            }
            """)
    Page<BookDocument> searchByKeyword(String keyword, Pageable pageable);
}
