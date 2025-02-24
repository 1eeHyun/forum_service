package com.ldh.forum.user.controller;

import com.ldh.forum.s3.S3Service;
import com.ldh.forum.user.model.Profile;
import com.ldh.forum.user.model.User;
import com.ldh.forum.user.service.ProfileService;
import com.ldh.forum.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final S3Service s3Service;

    public ProfileController(ProfileService profileService,
                             UserService userService,
                             S3Service s3Service) {

        this.profileService = profileService;
        this.userService = userService;
        this.s3Service = s3Service;
    }

    @GetMapping
    public String getProfile(@AuthenticationPrincipal UserDetails userDetails,
                             Model model) {

        User user = userService.findByUsername(userDetails.getUsername());
        Profile profile = profileService.getProfileByUser(user);
        model.addAttribute("profile", profile);
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        User user = userService.findByUsername(userDetails.getUsername());
        Profile profile = profileService.getProfileByUser(user);

        model.addAttribute("profile", profile);
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam("profileImage") MultipartFile profileImage,
                                @RequestParam("bio") String bio) throws IOException {

        User user = userService.findByUsername(userDetails.getUsername());
        Profile profile = profileService.getProfileByUser(user);

        if (!profileImage.isEmpty()) {
            String imageUrl = s3Service.uploadFile(profileImage);
            profileService.updateProfileImage(user, imageUrl);
        }

        profile.setBio(bio);
        profileService.save(profile);

        return "redirect:/";
    }

    @GetMapping("/{username}")
    public String viewUserProfile(@PathVariable String username,
                                  Model model) {

        User user = userService.findByUsername(username);
        if (user == null) {
            return "redirect:/";
        }

        Profile profile = profileService.getProfileByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);

        return "profile/view";
    }

}
