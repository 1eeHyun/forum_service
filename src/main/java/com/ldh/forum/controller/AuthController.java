package com.ldh.forum.controller;

import com.ldh.forum.service.UserService;
import com.ldh.forum.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors())
            return "register";

        try {
            userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail());
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }

        return "redirect:/login";
    }
}
