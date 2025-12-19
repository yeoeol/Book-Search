package com.example.booksearch.application.impl;

import com.example.booksearch.application.dto.BookDto;
import com.example.booksearch.global.parser.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final ExcelParser excelParser;

    public List<BookDto> getBookDtoListFromExcel(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("파일을 선택해주세요.");
        }
        log.info("파일명: {}", file.getOriginalFilename());
        log.info("파일 크기: {} bytes", file.getSize());

        return excelParser.parseExcel(file);
    }
}
