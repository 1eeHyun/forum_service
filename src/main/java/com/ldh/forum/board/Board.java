package com.ldh.forum.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Data
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Board(String title, String body, String author) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }
}
