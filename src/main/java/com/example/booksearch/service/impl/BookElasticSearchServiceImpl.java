package com.example.booksearch.service.impl;

import com.example.booksearch.domain.BookDocument;
import com.example.booksearch.dto.BookDto;
import com.example.booksearch.repository.BookElasticsearchRepository;
import com.example.booksearch.service.BookService;
import com.example.booksearch.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookElasticSearchServiceImpl implements BookService {

    private final BookElasticsearchRepository bookElasticsearchRepository;
    private final ExcelService excelService;

    @Override
    public void save(MultipartFile file) {
        List<BookDto> bookDtoList = excelService.getBookDtoListFromExcel(file);

        List<BookDocument> bookDocumentList = bookDtoList.stream()
                .map(BookDto::toBookDocument)
                .toList();

        bookElasticsearchRepository.saveAll(bookDocumentList);
    }

    @Override
    public Page<BookDto> search(String keyword, Pageable pageable) {
        Page<BookDocument> documentPage = bookElasticsearchRepository.findByTitleContaining(keyword, pageable);
        return documentPage.map(BookDto::from);
    }
}
