package com.ldh.forum.controller;

import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.service.BoardService;
import com.ldh.forum.user.model.Profile;
import com.ldh.forum.user.model.User;
import com.ldh.forum.user.service.ProfileService;
import com.ldh.forum.user.service.UserService;
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
    private final UserService userService;
    private final ProfileService profileService;

    public HomeController(BoardService boardService, UserService userService, ProfileService profileService) {
        this.boardService = boardService;
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping
    public String homePage(Model model) {

        // Community - Recent 5 posts
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Board> recentBoards = boardService.searchBoardsWithPagination("all", "", pageable);
        model.addAttribute("recentBoards", recentBoards.getContent());

        // Trendy - Top 5 posts of comments + like within a week
        List<Board> trendyBoards = boardService.getTop5RecentTrendyPosts();
        model.addAttribute("trendyBoards", trendyBoards);

        // user information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", authentication);

        // Profile image
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            User user = userService.findByUsername(authentication.getName());
            Profile profile = profileService.getProfileByUser(user);
            model.addAttribute("profileImageUrl", profile.getProfileImageUrl());
        }

        return "home"; // home.html
    }


    @GetMapping("/list")
    public List<Board> getAllBoards() {
        return boardService.getAllBoards();
    }

}
