package com.ldh.forum.user.service;

import com.ldh.forum.user.model.Profile;
import com.ldh.forum.user.model.User;
import com.ldh.forum.user.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile createDefaultProfile(User user) {
        Profile profile = new Profile();
        profile.setUser(user);
        return profileRepository.save(profile);
    }

    public void updateProfileImage(User user, String imageUrl) {
        Profile profile = profileRepository.findByUserId(user.getId());
        profile.setProfileImageUrl(imageUrl);
        profileRepository.save(profile);
    }

    public Profile getProfileByUser(User user) {
        return profileRepository.findByUserId(user.getId());
    }

    public void save(Profile profile) {
        profileRepository.save(profile);
    }
}
