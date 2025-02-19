package com.ldh.forum.service;

import com.ldh.forum.repository.UserRepository;
import com.ldh.forum.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfanityFilterService profanityFilterService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ProfanityFilterService profanityFilterService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profanityFilterService = profanityFilterService;
    }

    public User registerUser(String username, String password, String email) {

        if (profanityFilterService.containsProfanity(username))
            throw new RuntimeException("This username is not allowed.");

        if (userRepository.findByUsername(username).isPresent())
            throw new RuntimeException("The username already exists.");

        if (userRepository.findByEmail(email).isPresent())
            throw new RuntimeException("The email already exists.");

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(null, username, encodedPassword, email);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
