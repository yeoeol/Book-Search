package com.example.booksearch.application.impl;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import com.example.booksearch.application.dto.BookDto;
import com.example.booksearch.domain.repository.BookElasticsearchRepository;
import com.example.booksearch.domain.repository.BulkRepository;
import com.example.booksearch.application.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final ExcelService excelService;
    private final BulkRepository bulkRepository;
    private final BookElasticsearchRepository bookElasticsearchRepository;

    @Transactional
    public void save(MultipartFile file) {
        List<BookDto> bookDtoList = excelService.getBookDtoListFromExcel(file);
        List<Book> bookList = bookDtoList.stream()
                .map(BookDto::toBook)
                .toList();
        bulkRepository.bulkInsert("books", bookList);

        List<BookDocument> bookDocumentList = bookList.stream()
                        .map(BookDocument::from)
                        .toList();
        bookElasticsearchRepository.saveAll(bookDocumentList);
    }
}
