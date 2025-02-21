package com.ldh.forum.service;

import com.ldh.forum.board.Board;
import com.ldh.forum.repository.BoardRepository;
import com.ldh.forum.repository.CommentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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

    public Board createBoard(String title, String body, String author) {
        Board board = new Board(title, body, author);
        return boardRepository.save(board);
    }

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    public List<Board> searchBoardsByTitle(String query) {
        if (query == null || query.trim().isEmpty())
            return getAllBoards();

        return boardRepository.findByTitleContainingIgnoreCase(query);
    }

    public List<Board> searchBoardsByTitle(String query, String sort) {
        List<Board> boards = boardRepository.findByTitleContainingIgnoreCase(query);

        if ("oldest".equalsIgnoreCase(sort))
            boards.sort(Comparator.comparing(Board::getCreatedAt)); // sort old
        else
            boards.sort(Comparator.comparing(Board::getCreatedAt).reversed()); // sort recent

        return boards;
    }

    public List<Board> sortBoards(String sort) {
        List<Board> boards = boardRepository.findAll();

        if ("oldest".equalsIgnoreCase(sort))
            boards.sort(Comparator.comparing(Board::getCreatedAt));
        else
            boards.sort(Comparator.comparing(Board::getCreatedAt).reversed());

        return boards;
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

}
