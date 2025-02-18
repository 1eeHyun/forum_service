package com.ldh.forum.service;

import com.ldh.forum.board.Board;
import com.ldh.forum.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board createBoard(String title, String body) {
        Board board = new Board(title, body);
        return boardRepository.save(board);
    }

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public Optional<Board> updateBoard(Long id, String title, String body) {
        return boardRepository.findById(id).map(board -> {
            if (title != null && !title.isEmpty()) board.setTitle(title);
            if (body != null && !body.isEmpty()) board.setBody(body);
            return boardRepository.save(board);
        });
    }

    public boolean deleteBoard(Long id) {
        if (boardRepository.existsById(id)) {
            boardRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
