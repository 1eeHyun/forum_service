package com.ldh.forum.comment.repository;

import com.ldh.forum.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId);
    @Transactional
    void deleteByBoardId(Long boardId);
    List<Comment> findByBoardIdAndDeletedFalse(Long boardId);

    @Query("SELECT c.board.id FROM Comment c WHERE c.id = :commentId")
    Long findBoardIdByCommentId(@Param("commentId") Long commentId);
}
