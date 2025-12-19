package com.example.booksearch.presentation;

import com.example.booksearch.application.BookSearchService;
import com.example.booksearch.application.dto.BookDto;
import com.example.booksearch.application.impl.BookSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookSearchService bookSearchService;
    private final BookSyncService bookSyncService;

    @GetMapping("/")
    public String search(@RequestParam(required = false) String keyword,
                         @PageableDefault(size = 5) Pageable pageable,
                         Model model
    ) {
        if (StringUtils.hasText(keyword)) {
            Page<BookDto> result = bookSearchService.search(keyword, pageable);

            model.addAttribute("books", result);
            model.addAttribute("keyword", keyword);

            int startPage = Math.max(1, result.getPageable().getPageNumber()-4);
            int endPage = Math.min(result.getTotalPages(), result.getPageable().getPageNumber()+4);

            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
        }

        return "pages/search";
    }


    // mysql -> elastic-search 데이터 동기화 시 사용
    @GetMapping("/sync")
    public ResponseEntity<Void> syncBook() {
        bookSyncService.syncBooks();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sync-bulk")
    public ResponseEntity<Void> syncBook2() {
        bookSyncService.syncBooksByBulk();
        return ResponseEntity.noContent().build();
    }
}
