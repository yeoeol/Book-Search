package com.example.booksearch.application;

import com.example.booksearch.application.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookSearchService {
    Page<BookDto> search(String keyword, Pageable pageable);
}
