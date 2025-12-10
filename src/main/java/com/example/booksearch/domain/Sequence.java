package com.example.booksearch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sequences")
public class Sequence {

    @Id
    @Column(name = "name", length = 32)
    private String name;

    @Column(name = "next_val", nullable = false)
    private Long nextVal;
}
