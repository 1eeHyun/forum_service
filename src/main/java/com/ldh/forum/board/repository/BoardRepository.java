package com.ldh.forum.board.repository;


import com.ldh.forum.board.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // Search only title
    Page<Board> findByTitleContainingIgnoreCase(String query, Pageable pageable);
    // Search title or comment
    @Query("SELECT b FROM Board b LEFT JOIN b.comments c WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Board> searchByTitleOrCommentContent(@Param("query") String query, Pageable pageable);

    // Search only comment
    @Query("SELECT b FROM Board b LEFT JOIN b.comments c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Board> searchByCommentContent(@Param("query") String query, Pageable pageable);

    Page<Board> findAll(Pageable pageable);
}
