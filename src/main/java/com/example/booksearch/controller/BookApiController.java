package com.example.booksearch.controller;

import com.example.booksearch.dto.BookDto;
import com.example.booksearch.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookApiController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<BookDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(bookService.getAll(pageable));
    }
}
