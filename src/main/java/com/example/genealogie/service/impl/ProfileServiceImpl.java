package com.example.genealogie.service.impl;

import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Override
    public Profile create(Profile profile) {
        if(profileRepository.existsByUser_Id(profile.getUser().getId())) {
            throw new IllegalArgumentException("Un profil avec cet utilisateur existe déjà !");
        }
        return profileRepository.save(profile);
    }

    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Ce profil n'existe pas!"));
    }

    @Override
    public List<Profile> searchProfile(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return profileRepository.findAll();
        }
        return profileRepository.findByKeyword(keyword);
    }

    @Override
    public Profile update(Profile profile, User user) {
        if(!canEditProfile(profile, user)) {
            throw new SecurityException("You are not authorized to edit this profile");
        }
        profile.setUser(user);
        return profileRepository.save(profile);
    }

    private boolean canEditProfile(Profile profile, User currentUser) {
        if(currentUser.getRole() == UserRole.ADMIN) {
            return true;
        }

        return profile.getUser().getId().equals(currentUser.getId());
    }


}
