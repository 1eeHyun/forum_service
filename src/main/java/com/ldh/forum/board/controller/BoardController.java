package com.ldh.forum.board.controller;

import com.ldh.forum.comment.service.CommentService;
import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.service.BoardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/community")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    public BoardController(BoardService boardService, CommentService commentService) {
        this.boardService = boardService;
        this.commentService = commentService;
    }

    @GetMapping
    public String boardPage(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "15") int size,
                            @RequestParam(defaultValue = "all") String type,
                            @RequestParam(value = "query", required = false) String query,
                            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Board> boardPage = boardService.searchBoards(type, type, query, pageable);

        model.addAttribute("boardList", boardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", boardPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("query", query);
        model.addAttribute("type", type);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", authentication);

        return "community/community";
    }

    @GetMapping("/search")
    public String searchBoards(@RequestParam(value = "time", required = false, defaultValue = "all") String time,
                               @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                               @RequestParam(value = "query", required = false, defaultValue = "") String query,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "15") int size,
                               Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Board> searchResults = boardService.searchBoards(time, type, query, pageable);

        model.addAttribute("boardList", searchResults.getContent());
        model.addAttribute("currentPage", page + 1); // start from 1
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("query", query);
        model.addAttribute("type", type);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", authentication);

        return "community/community";
    }


    /**
     * New post (GET)
     * Form: create.html
     */
    @GetMapping("post/new")
    public String showCreateForm(Model model) {
        model.addAttribute("board", new Board());
        return "community/posts/create";
    }

    /**
     * New Post (POST)
     * Redirect: /boards
     */
    @PostMapping
    public String createBoard(@ModelAttribute Board board,
                              @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        boardService.createBoard(board.getTitle(), board.getBody(), userDetails.getUsername());
        return "redirect:/community";
    }

    /**
     * board Detail (GET)
     * @param id
     */
    @GetMapping("/threads/{id}")
    public String viewBoard(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            HttpSession session,
                            Model model) {

        Optional<Board> board = boardService.getBoardAndIncrementViews(id, session);
        if (board.isEmpty())
            return "redirect:/community/boards";

        model.addAttribute("board", board.get());
        model.addAttribute("comments", commentService.getCommentsByBoardId(id));

        if (userDetails != null) {
            model.addAttribute("loggedInUser", userDetails.getUsername()); // Add currently logged-in user
        }

        return "community/posts/detail";
    }

    @PostMapping("/threads/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return "redirect:/login";

        commentService.addComment(id, content, userDetails.getUsername());
        return "redirect:/community/threads/" + id;
    }

    @GetMapping("/threads/{id}/edit")
    public String showEditForm(@PathVariable Long id,Model model) {

        Optional<Board> board = boardService.getBoardById(id);
        if (board.isEmpty()) {
            return "redirect:/community/community";
        }
        model.addAttribute("board", board.get());
        return "community/posts/edit";
    }

    @PostMapping("/threads/{id}/update")
    public String updateBoard(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String body) {

        boardService.updateBoard(id, title, body);
        return "redirect:/community/threads/" + id;
    }

    @PostMapping("/threads/{id}/delete")
    public String deleteBoard(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        if (userDetails == null)
            return "redirect:/login";

        try {
            boardService.deleteBoard(id, userDetails);
        } catch (SecurityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "community/posts/detail";
        }

        return "redirect:/community";
    }
}
