package com.ldh.forum.board.repository;

import com.ldh.forum.board.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // Search only title
    List<Board> findByTitleContainingIgnoreCase(String query);
    // Search title or comment
    @Query("SELECT b FROM Board b LEFT JOIN b.comments c WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Board> searchByTitleOrCommentContent(@Param("query") String query);

    // Search only comment
    @Query("SELECT b FROM Board b LEFT JOIN b.comments c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Board> searchByCommentContent(@Param("query") String query);
}
