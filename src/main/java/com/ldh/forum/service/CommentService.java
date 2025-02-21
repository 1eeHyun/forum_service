package com.ldh.forum.service;

import com.ldh.forum.board.Board;
import com.ldh.forum.comment.Comment;
import com.ldh.forum.repository.BoardRepository;
import com.ldh.forum.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return commentRepository.findByBoardIdAndDeletedFalse(boardId);
    }

    public void addComment(Long boardId, String content, String author) {
        Optional<Board> board = boardRepository.findById(boardId);
        board.ifPresent(b -> {
            Comment comment = new Comment(content, author, b);
            commentRepository.save(comment);
        });
    }

    @Transactional
    public void deleteComment(Long commentId, String author) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.delete();
    }

    @Transactional
    public void updateComment(Long commentId, String newContent, String author) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.updateContent(newContent);
    }

    public void deleteCommentsByBoardId(Long boardId) {
        commentRepository.deleteByBoardId(boardId);
    }

    public Long getBoardIdByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        return comment.getBoard().getId();
    }

}
