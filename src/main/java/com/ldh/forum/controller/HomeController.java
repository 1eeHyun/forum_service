package com.ldh.forum.controller;

import com.ldh.forum.board.Board;
import com.ldh.forum.service.BoardService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/boards/search")
    public String searchBoard(@RequestParam("query") String query,
                              Model model) {

        model.addAttribute("boardList", boardService.searchBoardsByTitle(query));
        model.addAttribute("query", query);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", authentication);

        return "forum";
    }
}
