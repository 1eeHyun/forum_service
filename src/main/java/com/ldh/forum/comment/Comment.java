package com.ldh.forum.comment;

import com.ldh.forum.board.Board;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    public Comment(String content, String author, Board board) {
        this.content = content;
        this.author = author;
        this.board = board;
        this.createdAt = LocalDateTime.now();
    }
}
