package com.ldh.forum.controller;

import com.ldh.forum.board.Board;
import com.ldh.forum.service.BoardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("board", new Board());
        return "board/create";
    }

    @PostMapping
    public String createBoard(@ModelAttribute Board board,
                              @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        boardService.createBoard(board.getTitle(), board.getBody(), userDetails.getUsername());
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id, Model model) {

        Optional<Board> board = boardService.getBoardById(id);
        if (board.isEmpty()) {
            return "redirect:/boards";
        }
        model.addAttribute("board", board.get());
        return "board/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,Model model) {

        Optional<Board> board = boardService.getBoardById(id);
        if (board.isEmpty()) {
            return "redirect:/boards";
        }
        model.addAttribute("board", board.get());
        return "board/edit";
    }

    @PostMapping("/{id}/update")
    public String updateBoard(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String body) {

        boardService.updateBoard(id, title, body);
        return "redirect:/boards/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return "redirect:/";
    }


}
