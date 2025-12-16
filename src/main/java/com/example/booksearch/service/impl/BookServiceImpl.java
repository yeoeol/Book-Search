package com.example.booksearch.service.impl;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import com.example.booksearch.dto.BookDto;
import com.example.booksearch.repository.BookElasticsearchRepository;
import com.example.booksearch.repository.BookRepository;
import com.example.booksearch.repository.BulkRepository;
import com.example.booksearch.service.BookService;
import com.example.booksearch.service.ExcelService;
import lombok.RequiredArgsConstructor;
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
        Page<Book> pageBook = bookRepository.findByTitleStartsWith(keyword, pageable);
        return pageBook.map(BookDto::from);
    }
}
