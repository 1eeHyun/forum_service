package com.ldh.forum.controller;

import com.ldh.forum.board.Board;
import com.ldh.forum.service.BoardService;
import com.ldh.forum.service.CommentService;
import jakarta.servlet.http.HttpSession;
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
    private final CommentService commentService;

    public BoardController(BoardService boardService, CommentService commentService) {
        this.boardService = boardService;
        this.commentService = commentService;
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
    public String viewBoard(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            HttpSession session,
                            Model model) {

        Optional<Board> board = boardService.getBoardAndIncrementViews(id, session);
        if (board.isEmpty())
            return "redirect:/boards";

        model.addAttribute("board", board.get());
        model.addAttribute("comments", commentService.getCommentsByBoardId(id));

        if (userDetails != null) {
            model.addAttribute("loggedInUser", userDetails.getUsername()); // Add currently logged-in user
        }

        return "board/detail";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return "redirect:/login";

        commentService.addComment(id, content, userDetails.getUsername());
        return "redirect:/boards/" + id;
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
    public String deleteBoard(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        if (userDetails == null)
            return "redirect:/login";

        try {
            boardService.deleteBoard(id, userDetails);
        } catch (SecurityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "board/detail";
        }

        return "redirect:/";
    }
}
