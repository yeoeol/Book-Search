package com.example.booksearch.infrastructure.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    /**
     * 이미지 업로드 및 URL 반환
     */
    String uploadImage(MultipartFile file) throws IOException;
}
