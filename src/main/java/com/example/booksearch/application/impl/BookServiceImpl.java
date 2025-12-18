package com.example.booksearch.application.impl;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import com.example.booksearch.application.dto.BookDto;
import com.example.booksearch.domain.repository.BookElasticsearchRepository;
import com.example.booksearch.domain.repository.BookRepository;
import com.example.booksearch.domain.repository.BulkRepository;
import com.example.booksearch.application.BookService;
import com.example.booksearch.application.ExcelService;
import com.example.booksearch.infrastructure.image.gcs.GcsService;
import com.example.booksearch.infrastructure.naver.NaverBookSearchService;
import com.example.booksearch.infrastructure.naver.dto.response.NaverBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final ExcelService excelService;
    private final BulkRepository bulkRepository;
    private final BookRepository bookRepository;
    private final BookElasticsearchRepository bookElasticsearchRepository;
    private final NaverBookSearchService naverBookSearchService;
    private final GcsService gcsService;
    private final RestClient restClient = RestClient.create();

    @Value("${search.engine:mysql}")
    private String searchEngine;

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

    @Transactional
    public Page<BookDto> search(String keyword, Pageable pageable) {
        Page<BookDto> searchResult;
        if ("elastic-search".equalsIgnoreCase(searchEngine)) {
            // 엘라스틱 서치 검색
            searchResult = bookElasticsearchRepository.searchByKeyword(keyword, pageable)
                    .map(BookDto::from);
        }
        else {
            // MySQL 검색
            searchResult = bookRepository.findByTitleStartsWith(keyword, pageable)
                    .map(BookDto::from);
        }

        fillEmptyFields(searchResult.getContent());

        return searchResult;
    }

    private void fillEmptyFields(List<BookDto> books) {
        books.parallelStream().forEach(bookDto -> {
            boolean needUpdate = !StringUtils.hasText(bookDto.getIsbn()) ||
                                 !StringUtils.hasText(bookDto.getTitle()) ||
                                 !StringUtils.hasText(bookDto.getAuthor()) ||
                                 !StringUtils.hasText(bookDto.getDescription()) ||
                                 !StringUtils.hasText(bookDto.getImageUrl());
            if (needUpdate) {
                try {
                    NaverBookResponse response = naverBookSearchService.getBookResponse(bookDto.getIsbn(), bookDto.getTitle());
                    if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                        NaverBookResponse.Item item = response.getItems().get(0);

                        updateEmptyFields(bookDto, item);
                    }
                } catch (Exception e) {
                    log.error("오류 발생 (ISBN: {}, 책 제목: {})", bookDto.getIsbn(), bookDto.getTitle(), e);
                }
            }
        });
    }

    /**
     * 비어있는 필드를 API 데이터로 채우고 반영
     */
    private void updateEmptyFields(BookDto bookDto, NaverBookResponse.Item item) {
        boolean isChanged = false;

        if (!StringUtils.hasText(bookDto.getIsbn()) && StringUtils.hasText(item.getIsbn())) {
            bookDto.setIsbn(item.getIsbn());
            isChanged = true;
        }
        if (!StringUtils.hasText(bookDto.getTitle()) && StringUtils.hasText(item.getTitle())) {
            bookDto.setTitle(item.getTitle());
            isChanged = true;
        }
        if (!StringUtils.hasText(bookDto.getAuthor()) && StringUtils.hasText(item.getAuthor())) {
            bookDto.setAuthor(item.getAuthor());
            isChanged = true;
        }
        if (!StringUtils.hasText(bookDto.getDescription()) && StringUtils.hasText(item.getDescription())) {
            bookDto.setDescription(item.getDescription());
            isChanged = true;
        }
        if (!StringUtils.hasText(bookDto.getImageUrl()) && StringUtils.hasText(item.getImage())) {
            String imageUrl = uploadNaverImageToGcs(item.getImage());
            bookDto.setImageUrl(imageUrl);
            isChanged = true;
        }

        // 변경사항이 있을 때만 DB 저장 수행
        if (isChanged) {
            // MySQL 업데이트
            updateDB(bookDto.getId(), bookDto);

            // Elasticsearch 업데이트
            updateElasticsearch(bookDto);
        }
    }

    private String uploadNaverImageToGcs(String naverImageUrl) {
        try {
            byte[] imageBytes = restClient.get()
                    .uri(naverImageUrl)
                    .retrieve()
                    .body(byte[].class);

            if (imageBytes == null || imageBytes.length == 0) {
                return null;
            }
            return gcsService.uploadImageBytes(imageBytes, "image/jpeg");

        } catch (Exception e) {
            log.error("이미지 다운로드/업로드 실패 (URL: {})", naverImageUrl, e);
            return null;
        }
    }

    // DB 저장
    private void updateDB(Long id, BookDto updatedDto) {
        bookRepository.findById(id).ifPresent(book -> {
            if (StringUtils.hasText(updatedDto.getIsbn())) book.updateIsbn(updatedDto.getIsbn());
            if (StringUtils.hasText(updatedDto.getTitle())) book.updateTitle(updatedDto.getTitle());
            if (StringUtils.hasText(updatedDto.getAuthor())) book.updateAuthor(updatedDto.getAuthor());
            if (StringUtils.hasText(updatedDto.getDescription())) book.updateDescription(updatedDto.getDescription());
            if (StringUtils.hasText(updatedDto.getImageUrl())) book.updateImageUrl(updatedDto.getImageUrl());

            bookRepository.save(book);
        });
    }

    // Elasticsearch 저장
    private void updateElasticsearch(BookDto updatedDto) {
        bookElasticsearchRepository.findById(String.valueOf(updatedDto.getId())).ifPresent(doc -> {

            if (StringUtils.hasText(updatedDto.getIsbn())) doc.updateIsbn(updatedDto.getIsbn());
            if (StringUtils.hasText(updatedDto.getTitle())) doc.updateTitle(updatedDto.getTitle());
            if (StringUtils.hasText(updatedDto.getAuthor())) doc.updateAuthor(updatedDto.getAuthor());
            if (StringUtils.hasText(updatedDto.getDescription())) doc.updateDescription(updatedDto.getDescription());
            if (StringUtils.hasText(updatedDto.getImageUrl())) doc.updateImageUrl(updatedDto.getImageUrl());

            bookElasticsearchRepository.save(doc);
        });
    }
}
