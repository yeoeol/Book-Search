package com.example.booksearch.parser;

import com.example.booksearch.domain.enums.BookExcelColumn;
import com.example.booksearch.dto.BookDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class ExcelParser {

    public List<BookDto> parseExcel(MultipartFile file) {
        List<BookDto> bookDtoList = new ArrayList<>();

        try {
            // 파일 확장자 검증
            validateExt(file.getOriginalFilename());

            // Excel 파일 처리
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                throw new RuntimeException("헤더 Row가 없습니다.");
            }
            Row headerRow = rowIterator.next();

            Map<BookExcelColumn, Integer> columnMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String headerValue = getCellValue(cell);
                // Enum을 순회하며 매칭되는 컬럼 찾기
                for (BookExcelColumn column : BookExcelColumn.values()) {
                    if (column.isMatch(headerValue)) {
                        columnMap.put(column, cell.getColumnIndex());
                        break; // 매칭되면 다음 셀로
                    }
                }
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                BookDto book = BookDto.builder()
                        .isbn(getSafeValue(row, columnMap.get(BookExcelColumn.ISBN)))
                        .title(getSafeValue(row, columnMap.get(BookExcelColumn.TITLE)))
                        .author(getSafeValue(row, columnMap.get(BookExcelColumn.AUTHOR)))
                        .publisher(getSafeValue(row, columnMap.get(BookExcelColumn.PUBLISHER)))
                        .price(parsePrice(getSafeValue(row, columnMap.get(BookExcelColumn.PRICE))))
                        .publishedDate(getSafeValue(row, columnMap.get(BookExcelColumn.PUBLISHED_DATE)))
                        .build();
                bookDtoList.add(book);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        }
        return bookDtoList;
    }

    private void validateExt(String filename) {
        if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
            throw new RuntimeException("Excel 파일만 업로드 가능합니다.");
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private String getSafeValue(Row row, Integer columnIndex) {
        if (columnIndex == null) return null; // 해당 컬럼이 엑셀에 없으면 null
        Cell cell = row.getCell(columnIndex);
        return getCellValue(cell);
    }

    private Integer parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isBlank()) return 0;
        try {
            return Integer.parseInt(priceStr.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
