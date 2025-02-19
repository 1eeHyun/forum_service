package com.ldh.forum.controller;

import com.ldh.forum.service.EmailService;
import com.ldh.forum.service.UserService;
import com.ldh.forum.user.User;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("step", "email");
        return "register";
    }

    @PostMapping("/register/send-code")
    public String sendVerificationCode(@RequestParam("email") String email,
                                       Model model) {
        if (userService.existsByEmail(email)) {
            model.addAttribute("errorMessage", "This email is already registered.");
            model.addAttribute("step", "email");
            return "register";
        }

        String code = emailService.generateVerificationCode();
        verificationCodes.put(email, code);

        if (emailService.sendVerificationEmail(email, code)) {
            model.addAttribute("step", "verify");
            model.addAttribute("email", email);
        } else {
            model.addAttribute("errorMessage", "Failed to send verification email.");
            model.addAttribute("step", "email");
        }

        return "register";
    }

    @PostMapping("/register/verify-code")
    public String verifyCode(@RequestParam("email") String email,
                             @RequestParam("code") String code,
                             Model model) {

        String expectedCode = verificationCodes.get(email);
        if (expectedCode != null && expectedCode.equals(code)) {
            verifiedEmails.put(email, true);
            model.addAttribute("step", "register");
            model.addAttribute("user", new User());
            model.addAttribute("email", email);
        } else {
            model.addAttribute("step", "verify");
            model.addAttribute("errorMessage", "Invalid verification code.");
        }

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid User user,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            model.addAttribute("step", "register");
            model.addAttribute("email", user.getEmail());
            return "register"; // Unverified email
        }

        // Check if email is verified
        Boolean isEmailVerified = verifiedEmails.get(user.getEmail());
        if (isEmailVerified == null || !isEmailVerified) {
            model.addAttribute("errorMessage", "Email verification is required.");
            model.addAttribute("step", "email");
            return "register";
        }

        try {
            userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail());
            verifiedEmails.remove(user.getEmail());
            verificationCodes.remove(user.getEmail());

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("step", "register");
            model.addAttribute("email", user.getEmail());
            return "register";
        }

        return "redirect:/login";
    }
}
