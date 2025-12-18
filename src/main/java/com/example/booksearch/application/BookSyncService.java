package com.example.booksearch.application;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import com.example.booksearch.domain.repository.BookElasticsearchRepository;
import com.example.booksearch.domain.repository.BookRepository;
import com.example.booksearch.domain.repository.BulkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSyncService {

    private final BookRepository bookRepository;
    private final BookElasticsearchRepository bookElasticsearchRepository;
    private final BulkRepository bulkRepository;

    @Transactional(readOnly = true)
    public void syncBooks() {
        executeSyncProcess(bookElasticsearchRepository::saveAll);
    }

    @Transactional(readOnly = true)
    public void syncBooksByBulk() {
        executeSyncProcess(bookDocuments ->
                bulkRepository.bulkInsertForElasticsearch("books", bookDocuments)
        );
    }

    /**
     * 공통 로직 템플릿
     * @param saveStrategy : JPA saveAll 또는 Bulk Insert를 함수로 받음
     */
    private void executeSyncProcess(Consumer<List<BookDocument>> saveStrategy) {
        int page = 0;
        int size = 1000;

        while (true) {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Book> bookPage = bookRepository.findAll(pageRequest);

            if (bookPage.isEmpty()) {
                break;
            }

            List<BookDocument> bookDocumentList = bookPage.stream()
                    .map(BookDocument::from)
                    .toList();

            saveStrategy.accept(bookDocumentList);

            log.info("Book -> BookDocument 동기화 진행 중 : {}", (page+1)*size);
            page++;
        }

        log.info("Book -> BookDocument 동기화 완료");
    }
}
