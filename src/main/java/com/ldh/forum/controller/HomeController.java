package com.ldh.forum.controller;

import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.service.BoardService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final BoardService boardService;

    public HomeController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public String boardPage(Model model) {

        List<Board> boardList = boardService.getAllBoards();
        model.addAttribute("boardList", boardList);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", authentication);
        return "forum";
    }

    @GetMapping("/list")
    public List<Board> getAllBoards() {
        return boardService.getAllBoards();
    }

}
