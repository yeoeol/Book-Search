package com.example.booksearch.presentation;

import com.example.booksearch.infrastructure.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public String uploadTest(@RequestPart MultipartFile file) throws IOException {
        return imageService.uploadImage(file);
    }

}
