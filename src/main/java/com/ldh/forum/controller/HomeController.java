package com.ldh.forum.controller;

import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public String homePage(Model model) {

        // Recent five-posts
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Board> recentBoards = boardService.searchBoardsWithPagination("all", "", pageable);

        model.addAttribute("recentBoards", recentBoards.getContent());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", authentication);

        return "home"; // home.html
    }

    @GetMapping("/list")
    public List<Board> getAllBoards() {
        return boardService.getAllBoards();
    }

}
