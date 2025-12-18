package com.example.booksearch.application;

import com.example.booksearch.application.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    void save(MultipartFile file);

    Page<BookDto> search(String keyword, Pageable pageable);
}
