package com.ldh.forum.board.service;

import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.repository.BoardRepository;
import com.ldh.forum.comment.repository.CommentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public BoardService(BoardRepository boardRepository, CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    public Board createBoard(String title, String body, String author, String imageUrl, String category) {
        Board board = new Board();
        board.setTitle(title);
        board.setBody(body);
        board.setAuthor(author);
        board.setImageUrl(imageUrl);
        board.setCategory(category);
        return boardRepository.save(board);
    }

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public Page<Board> searchBoards(String time,
                                    String type,
                                    String query,
                                    Pageable pageable) {


        // Default search
        if (query == null || query.isEmpty()) {
            return boardRepository.findAll(pageable);
        }

        if ("all".equals(type)) {
            return boardRepository.searchByTitleOrCommentContent(query, pageable);
        } else if ("post".equals(type)) {
            return boardRepository.findByTitleContainingIgnoreCase(query, pageable);
        } else if ("comment".equals(type)) {
            return boardRepository.searchByCommentContent(query, pageable);
        }

        return Page.empty(pageable);
    }

    public Optional<Board> updateBoard(Long id, String title, String body) {
        return boardRepository.findById(id).map(board -> {
            if (title != null && !title.isEmpty()) board.setTitle(title);
            if (body != null && !body.isEmpty()) board.setBody(body);
            return boardRepository.save(board);
        });
    }

    @Transactional
    public Optional<Board> getBoardAndIncrementViews(Long id, HttpSession session) {
        String viewKey = "views_board_" + id;

        if (session.getAttribute(viewKey) == null) {
            boardRepository.findById(id).ifPresent(board -> {
                board.setViews(board.getViews() + 1);
                boardRepository.save(board);
                session.setAttribute(viewKey, true); // save view history
            });
        }
        return boardRepository.findById(id);
    }

    @Transactional
    public boolean deleteBoard(Long id, UserDetails userDetails) {

        Optional<Board> boardOptional = boardRepository.findById(id);

        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();

            if (!board.getAuthor().equals(userDetails.getUsername()))
                throw new SecurityException("You are not authorized to delete this post.");

            // delete every comment that is on the board
            commentRepository.deleteByBoardId(board.getId());

            // delete the board
            boardRepository.delete(board);

            return true;
        }

        return false;
    }

    /**
     * Page functionality
     */
    public Page<Board> searchBoardsWithPagination(String type, String query, Pageable pageable) {

        if (query == null || query.isEmpty()) {
            return boardRepository.findAll(pageable);
        }

        return switch (type) {
            case "post" -> boardRepository.findByTitleContainingIgnoreCase(query, pageable);
            case "comment" -> boardRepository.searchByCommentContent(query, pageable);
            default -> boardRepository.searchByTitleOrCommentContent(query, pageable);
        };
    }

    public Page<Board> getBoardsByCategory(String category, Pageable pageable) {
        return boardRepository.findByCategoryIgnoreCase(category, pageable);
    }

    public List<Board> getTop5RecentTrendyPosts() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return boardRepository.findTop5ByCategoryAndRecentWeek("Community", oneWeekAgo);
    }
}
