package com.example.booksearch.infrastructure.naver;

import com.example.booksearch.infrastructure.naver.dto.response.NaverBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;


@Slf4j
@Service
@RequiredArgsConstructor
public class NaverBookSearchService {

    private final RestClient restClient;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public NaverBookSearchService() {
        this.restClient = RestClient.create("https://openapi.naver.com");
    }

    public NaverBookResponse getBookResponse(String isbn, String title) {
        try {
            String query = StringUtils.hasText(title) ? title : isbn;
            return restClient.get()
                    .uri("v1/search/book.json?query={query}&display=1", query)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .retrieve()
                    .body(NaverBookResponse.class);
        } catch (Exception e) {
            log.error("네이버 API 호출 실패 - query: {}", isbn != null ? isbn : title, e);
        }
        return null;
    }
}
