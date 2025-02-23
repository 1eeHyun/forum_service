package com.ldh.forum.comment.service;

import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.repository.BoardRepository;
import com.ldh.forum.comment.model.Comment;
import com.ldh.forum.comment.repository.CommentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public Comment addComment(Long boardId, String content, String author) {

        Board board = boardRepository.findById(boardId).orElseThrow();
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setContent(content);
        comment.setAuthor(author);
        return commentRepository.save(comment);
    }

    public Comment addReply(Long parentId, String content, String author) {

        Comment parentComment = commentRepository.findById(parentId).orElseThrow();
        Comment reply = new Comment();
        reply.setBoard(parentComment.getBoard());
        reply.setContent(content);
        reply.setAuthor(author);
        reply.setParent(parentComment);
        return commentRepository.save(reply);
    }

    public List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardIdAndParentIsNullOrderByCreatedAtAsc(boardId);
    }

    public List<Comment> getReplies(Long parentId) {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(parentId);
    }

    @Transactional
    public void deleteComment(Long commentId, String author) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getAuthor().equals(author))
            throw new SecurityException("You are not authorized to delete this comment.");

        if (!comment.getReplies().isEmpty()) {
            comment.setContent("Deleted comment.");
            commentRepository.save(comment);
            return;
        }

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
