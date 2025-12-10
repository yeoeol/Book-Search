공공데이터 포털에서 "도서" 키워드를 검색하여, 전국구 도서관들의 도서 정보를 엑셀(.xls, .xlsx)파일로 다운받았습니다. 
https://www.data.go.kr/tcs/dss/selectDataSetList.do?keyword=%EB%8F%84%EC%84%9C

다운받은 파일을 실행 중인 스프링부트 서버에 업로드하여 엑셀 파일을 읽도록 했습니다. 모든 행을 각 열의 제목에 맞게 값을 주입시켜 Book 엔티티를 생성한 후 DB에 bulk insert를 수행했습니다.
이때 MySQL의 rewriteBatchStatements 옵션을 활성화시켜 Multi-queries를 보낼 수 있도록 했습니다.

총 **1,028,503 rows** 의 데이터를 대상으로 검색을 진행했습니다.

```sql
SELECT title FROM BOOKS
WHERE title like '해리%';

SELECT publisher FROM BOOKS
WHERE publisher like '동아%';
```
|구분|TITLE|PUBLISHER|
|------|---|---|
|INDEX X|730ms|444ms|
|INDEX O|2ms|1ms|
|Rows|4건|2078건| 

사용한 SQL 목록
-- 

**책 정보 테이블**
```sql
create table books(
    price          int           null,
    id             bigint        not null primary key,
    author         varchar(1000) null,
    description    text          null,
    image_url      varchar(255)  null,
    isbn           varchar(255)  null,
    published_date varchar(255)  null,
    publisher      varchar(255)  null,
    title          varchar(500)  null
);
```

**pk 채번 테이블**
```sql
CREATE TABLE sequences (
   name VARCHAR(32) NOT NULL,  -- 할당된 테이블 명
   next_val BIGINT NOT NULL,
   PRIMARY KEY (name)
);
```


**INDEX**
```sql
-- title 컬럼 인덱스 생성
CREATE INDEX idx_title
ON books(title);

-- 인덱스 확인
show index from books;

-- 인덱스 삭제
ALTER TABLE books
DROP INDEX idx_title;
```

**실행계획 예측 및 분석**
```sql
-- 실행계획 예측
EXPLAIN
SELECT title FROM BOOKS
WHERE title = '물고기 아이';

-- 쿼리 분석
EXPLAIN ANALYZE
SELECT title FROM BOOKS
WHERE title = '물고기 아이';
```  
----
**관련한 내용의 블로그**

https://yeoeol.tistory.com/20
