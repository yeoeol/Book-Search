package com.example.booksearch.application;

import com.example.booksearch.domain.Book;
import com.example.booksearch.application.dto.BookApiResponse;
import com.example.booksearch.domain.repository.BulkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private static final String key = "9793674d5ce164e502a6a401de25e969b0b6c18e2212c2396eff11c640d3d131";

    private final BulkRepository bulkRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient = RestClient.create("https://www.nl.go.kr/NL/search/openApi/searchKolisNet.do");

    public void requestByKeyword(String keyword) {
        BookApiResponse body = restClient.get()
                .uri("?key={key}&kwd={keyword}", key, keyword)
                .retrieve()
                .body(BookApiResponse.class);
        List<Book> list = getBookList(body);
        System.out.println(list);
//        bulkRepository.bulkInsert(list);
    }

    private static List<Book> getBookList(BookApiResponse body) {
        BookApiResponse.Result result = body.getResult();
        List<BookApiResponse.Item> items = result.getItems();
        return items.stream()
                .map(BookApiResponse.Item::toBook)
                .toList();
    }
}
