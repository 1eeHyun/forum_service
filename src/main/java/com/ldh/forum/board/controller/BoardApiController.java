package com.ldh.forum.board.controller;

import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardApiController {

    private final BoardService boardService;

    public BoardApiController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/{category}")
    public Page<Board> getBoardsByCategory(@PathVariable String category,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return boardService.getBoardsByCategory(category, pageable);
    }
}
