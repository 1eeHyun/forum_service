package com.ldh.forum.comment.service;

import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.repository.BoardRepository;
import com.ldh.forum.comment.model.Comment;
import com.ldh.forum.comment.repository.CommentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository, ApplicationEventPublisher eventPublisher) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.eventPublisher = eventPublisher;
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

        if (!comment.getAuthor().equals(author))
            throw new SecurityException("You are not authorized to delete this comment.");

        Board board = comment.getBoard();

        board.removeComment(comment);

        commentRepository.delete(comment);
    }

    @Transactional
    public void updateComment(Long commentId, String newContent, String author) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.updateContent(newContent);
    }

    public Long getBoardIdByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        return comment.getBoard().getId();
    }
}
