package com.example.booksearch.application.impl;

import com.example.booksearch.application.dto.BookDto;
import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookDocument;
import com.example.booksearch.domain.repository.BookElasticsearchRepository;
import com.example.booksearch.domain.repository.BookRepository;
import com.example.booksearch.infrastructure.naver.NaverBookSearchService;
import com.example.booksearch.infrastructure.naver.dto.response.NaverBookResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class BookSearchServiceImplTest {

    @InjectMocks
    BookSearchServiceImpl bookSearchService;

    @Mock BookRepository bookRepository;
    @Mock BookElasticsearchRepository bookElasticsearchRepository;
    @Mock NaverBookSearchService naverBookSearchService;

    @Test
    @DisplayName("MySQL 검색 엔진이 설정되어 있으면 BookRepository가 호출되어야 한다")
    void search_with_mysql() {
        // given
        ReflectionTestUtils.setField(bookSearchService, "searchEngine", "mysql");

        String keyword = "해리";
        Pageable pageable = PageRequest.of(0, 10);

        Book mockBook = Book.builder()
                .id(1L).title("해리포터").isbn("1234").author("롤링").description("재밌음").imageUrl("url")
                .build();
        Page<Book> mockPage = new PageImpl<>(List.of(mockBook));

        given(bookRepository.findByTitleStartsWith(eq(keyword), any(Pageable.class)))
                .willReturn(mockPage);

        // when
        Page<BookDto> result = bookSearchService.search(keyword, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("해리포터");

        // 검증: MySQL은 호출되고, 엘라스틱서치는 호출되지 않아야 함
        verify(bookRepository).findByTitleStartsWith(eq(keyword), any(Pageable.class));
        verify(bookElasticsearchRepository, never()).searchByKeyword(any(), any());
    }

    @Test
    @DisplayName("Elasticsearch 엔진이 설정되어 있으면 BookElasticsearchRepository가 호출되어야 한다")
    void search_with_elasticsearch() {
        // given
        ReflectionTestUtils.setField(bookSearchService, "searchEngine", "elastic-search");

        String keyword = "해리";
        Pageable pageable = PageRequest.of(0, 10);

        // Elasticsearch 도큐먼트 반환 설정
        BookDocument mockDoc = BookDocument.builder()
                .id(1L).title("해리포터 ES").isbn("1234").author("롤링").description("ES 검색").imageUrl("url")
                .build();
        Page<BookDocument> mockPage = new PageImpl<>(List.of(mockDoc));

        given(bookElasticsearchRepository.searchByKeyword(eq(keyword), any(Pageable.class)))
                .willReturn(mockPage);

        // when
        Page<BookDto> result = bookSearchService.search(keyword, pageable);

        // then
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("해리포터 ES");
        assertThat(result.getContent().get(0).getIsbn()).isEqualTo("1234");
        assertThat(result.getContent().get(0).getAuthor()).isEqualTo("롤링");

        // 검증: 엘라스틱서치는 호출되고, MySQL은 호출되지 않아야 함
        verify(bookElasticsearchRepository).searchByKeyword(eq(keyword), any(Pageable.class));
        verify(bookRepository, never()).findByTitleStartsWith(any(), any());
    }

    @Test
    @DisplayName("검색 결과 중 비어있는 필드가 있으면 Naver API를 호출하여 보정한다")
    void search_fills_empty_fields() {
        // given
        ReflectionTestUtils.setField(bookSearchService, "searchEngine", "mysql");

        // 비어있는 필드가 있는 책 데이터 생성
        Book emptyBook = Book.builder()
                .id(1L).title("해리포터").isbn("1234").author("").description("설명").imageUrl("url")
                .build();

        given(bookRepository.findByTitleStartsWith(any(), any()))
                .willReturn(new PageImpl<>(List.of(emptyBook)));

        // 네이버 API가 호출될 것을 예상하여 Mocking
        NaverBookResponse.Item naverItem = new NaverBookResponse.Item("해리포터", "1", "1234", "url", "설명");
        given(naverBookSearchService.getBookResponse("1234", "해리포터"))
                .willReturn(new NaverBookResponse(List.of(naverItem)));

        // when
        bookSearchService.search("해리", PageRequest.of(0, 10));

        // then
        // 네이버 검색 서비스가 호출되었는지 확인
        verify(naverBookSearchService).getBookResponse("1234", "해리포터");

        // 데이터가 변경되었으므로 DB 업데이트 로직도 호출되었는지 확인
        verify(bookRepository).findById(1L);
    }
}