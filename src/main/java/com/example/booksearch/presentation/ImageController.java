package com.example.booksearch.presentation;

import com.example.booksearch.infrastructure.image.ImageService;
import com.example.booksearch.infrastructure.naver.NaverBookSearchService;
import com.example.booksearch.infrastructure.naver.dto.response.NaverBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final NaverBookSearchService naverBookSearchService;

    @PostMapping("/upload")
    public String uploadTest(@RequestPart MultipartFile file) throws IOException {
        return imageService.uploadImage(file);
    }

    @GetMapping("/naver-search-test")
    public ResponseEntity<NaverBookResponse> naverSearch(
            @RequestParam String isbn,
            @RequestParam String title
    ) {
        return ResponseEntity.ok(naverBookSearchService.getBookResponse(isbn, title));
    }
}
