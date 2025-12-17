package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import com.example.booksearch.repository.BookElasticsearchRepository;
import com.example.booksearch.repository.BookRepository;
import com.example.booksearch.repository.BulkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSyncService {

    private final BookRepository bookRepository;
    private final BookElasticsearchRepository bookElasticsearchRepository;
    private final BulkRepository bulkRepository;

    @Transactional(readOnly = true)
    public void syncBooks() {
        int page = 0;
        int size = 1000;

        while (true) {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Book> bookPage = bookRepository.findAll(pageRequest);

            if (bookPage.isEmpty()) {
                break;
            }

            List<BookDocument> bookDocumentList = bookPage.stream()
                    .map(book -> BookDocument.builder()
                            .isbn(book.getIsbn())
                            .title(book.getTitle())
                            .author(book.getAuthor())
                            .publisher(book.getPublisher())
                            .price(book.getPrice())
                            .description(book.getDescription())
                            .publishedDate(book.getPublishedDate())
                            .imageUrl(book.getImageUrl())
                            .build()
                    ).toList();

            bookElasticsearchRepository.saveAll(bookDocumentList);

            log.info("Book -> BookDocument 동기화 진행 중 : {}", (page+1)*size);
            page++;
        }

        log.info("Book -> BookDocument 동기화 완료");
    }

    @Transactional(readOnly = true)
    public void syncBooksByBulk() {
        List<BookDocument> bookDocumentList;
        int page = 0;
        int size = 1000;

        while (true) {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Book> bookPage = bookRepository.findAll(pageRequest);

            if (bookPage.isEmpty()) {
                break;
            }

            bookDocumentList = bookPage.stream()
                    .map(book -> BookDocument.builder()
                            .isbn(book.getIsbn())
                            .title(book.getTitle())
                            .author(book.getAuthor())
                            .publisher(book.getPublisher())
                            .price(book.getPrice())
                            .description(book.getDescription())
                            .publishedDate(book.getPublishedDate())
                            .imageUrl(book.getImageUrl())
                            .build()
                    )
                    .collect(Collectors.toList());

            bulkRepository.bulkInsertForElasticsearch("books", bookDocumentList);

            log.info("Book -> BookDocument 동기화 진행 중 : {}", (page+1)*size);
            page++;

            bookDocumentList.clear();
        }

        log.info("Book -> BookDocument 동기화 완료");
    }
}
