package com.ldh.forum.board.repository;


import com.ldh.forum.board.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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

    // retrieve by categories including paging
    Page<Board> findByCategoryIgnoreCase(String category, Pageable pageable);

    @Query("SELECT b FROM Board b LEFT JOIN b.comments c " +
            "WHERE b.category = :category AND b.createdAt >= :startDate " +
            "GROUP BY b " +
            "ORDER BY COUNT(c) + b.likes DESC")
    List<Board> findTop5ByCategoryAndRecentWeek(@Param("category") String category, @Param("startDate") LocalDateTime startDate);
}
