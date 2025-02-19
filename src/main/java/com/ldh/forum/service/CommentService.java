package com.ldh.forum.service;

import com.ldh.forum.board.Board;
import com.ldh.forum.comment.Comment;
import com.ldh.forum.repository.BoardRepository;
import com.ldh.forum.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
    }

    public List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    public void addComment(Long boardId, String content, String author) {
        Optional<Board> board = boardRepository.findById(boardId);
        board.ifPresent(b -> {
            Comment comment = new Comment(content, author, b);
            commentRepository.save(comment);
        });
    }

    public void deleteCommentsByBoardId(Long boardId) {
        commentRepository.deleteByBoardId(boardId);
    }
}
