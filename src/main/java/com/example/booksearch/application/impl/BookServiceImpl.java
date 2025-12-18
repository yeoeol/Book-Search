package com.example.booksearch.application.impl;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import com.example.booksearch.application.dto.BookDto;
import com.example.booksearch.domain.repository.BookElasticsearchRepository;
import com.example.booksearch.domain.repository.BookRepository;
import com.example.booksearch.domain.repository.BulkRepository;
import com.example.booksearch.application.BookService;
import com.example.booksearch.application.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final ExcelService excelService;
    private final BulkRepository bulkRepository;
    private final BookRepository bookRepository;
    private final BookElasticsearchRepository bookElasticsearchRepository;

    @Value("${search.engine:mysql}")
    private String searchEngine;

    @Transactional
    public void save(MultipartFile file) {
        List<BookDto> bookDtoList = excelService.getBookDtoListFromExcel(file);
        List<Book> bookList = bookDtoList.stream()
                .map(BookDto::toBook)
                .toList();

        List<BookDocument> bookDocumentList = bookDtoList.stream()
                .map(BookDto::toBookDocument)
                .toList();

        bulkRepository.bulkInsert("books", bookList);
        bookElasticsearchRepository.saveAll(bookDocumentList);
    }

    @Transactional(readOnly = true)
    public Page<BookDto> search(String keyword, Pageable pageable) {
        if ("elastic-search".equalsIgnoreCase(searchEngine)) {
            // 엘라스틱 서치 검색
            return bookElasticsearchRepository.searchByKeyword(keyword, pageable)
                    .map(BookDto::from);
        }
        else {
            // MySQL 검색
            return bookRepository.findByTitleStartsWith(keyword, pageable)
                    .map(BookDto::from);
        }
    }
}
