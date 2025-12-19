package com.example.booksearch.application;

import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    void save(MultipartFile file);
}
