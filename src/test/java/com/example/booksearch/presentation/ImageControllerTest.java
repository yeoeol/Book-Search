package com.example.booksearch.presentation;

import com.example.booksearch.infrastructure.image.gcs.GcsService;
import com.example.booksearch.infrastructure.naver.NaverBookSearchService;
import com.example.booksearch.infrastructure.naver.dto.response.NaverBookResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MockMvcTester mvcTester;

    @MockitoBean
    GcsService gcsService;

    @MockitoBean
    NaverBookSearchService naverBookSearchService;

    // http://localhost:8080/naver-search-test?isbn=1111&title=해리포터
    @Test
    @DisplayName("naverSearch API 호출 결과 테스트")
    void search() throws Exception {
        given(naverBookSearchService.getBookResponse("1111", "해리포터")).willReturn(
                new NaverBookResponse(
                        new ArrayList<>(
                                List.of(new NaverBookResponse.Item("해리포터", "해리", "1111", "image", "desc"))
                        )
                )
        );

        mockMvc.perform(
                get("/naver-search-test?isbn=%s&title=%s".formatted("1111", "해리포터")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").exists())
            .andExpect(jsonPath("$.items[0].title").exists())
            .andExpect(jsonPath("$.items[0].author").exists())
            .andExpect(jsonPath("$.items[0].isbn").exists())
            .andExpect(jsonPath("$.items[0].image").exists())
            .andExpect(jsonPath("$.items[0].description").exists())
            .andDo(print());

        verify(naverBookSearchService).getBookResponse("1111", "해리포터");
    }

    // http://localhost:8080/naver-search-test?isbn=1111&title=해리포터
    @Test
    @DisplayName("naverSearch API 호출 결과 테스트 - V2")
    void searchV2() throws Exception {
        given(naverBookSearchService.getBookResponse("1111", "해리포터")).willReturn(
                new NaverBookResponse(
                        new ArrayList<>(
                                List.of(new NaverBookResponse.Item("해리포터", "해리", "1111", "image", "desc"))
                        )
                )
        );

        assertThat(mvcTester.get().uri("/naver-search-test?isbn=%s&title=%s".formatted("1111", "해리포터"))
                .accept(MediaType.APPLICATION_JSON))
                .hasStatusOk();

        verify(naverBookSearchService).getBookResponse("1111", "해리포터");
    }
}