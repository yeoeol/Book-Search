package com.example.booksearch.presentation;

import com.example.booksearch.application.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelApiController {

    private final BookService bookService;

    @PostMapping("/upload")
    public ResponseEntity<Void> handleFileUpload(@RequestParam("file") MultipartFile file) {
        bookService.save(file);
        return ResponseEntity.noContent().build();
    }
}
