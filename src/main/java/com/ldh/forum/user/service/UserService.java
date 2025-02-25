package com.ldh.forum.user.service;

import com.ldh.forum.board.common.service.ProfanityFilterService;
import com.ldh.forum.user.model.User;
import com.ldh.forum.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfanityFilterService profanityFilterService;
    private final ProfileService profileService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ProfanityFilterService profanityFilterService,
                       ProfileService profileService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profanityFilterService = profanityFilterService;
        this.profileService = profileService;
    }

    public User registerUser(String username, String password, String email) {

        if (username.length() < 5 || username.length() > 20)
            throw new RuntimeException("Username must be between 5 and 20 characters.");

        if (profanityFilterService.containsProfanity(username))
            throw new RuntimeException("This username is not allowed.");

        if (userRepository.findByUsername(username).isPresent())
            throw new RuntimeException("The username already exists.");

        if (userRepository.findByEmail(email).isPresent())
            throw new RuntimeException("The email already exists.");

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(null, username, encodedPassword, email);
        User savedUser = userRepository.save(user);

        profileService.createDefaultProfile(user);

        return savedUser;
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return null;
    }
}
