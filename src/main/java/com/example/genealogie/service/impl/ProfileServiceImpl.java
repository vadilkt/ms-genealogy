package com.example.genealogie.service.impl;

import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.repository.UserRepository;
import com.example.genealogie.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public Profile create(Profile profile, User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN && profile.getUser() != null
                && profileRepository.existsByUser_Id(profile.getUser().getId())) {
            throw new IllegalArgumentException("Un profil avec cet utilisateur existe déjà !");
        }
        return profileRepository.save(profile);
    }

    @Override
    public Profile assignUser(Long profileId, Long userId) {
        Profile profile = getProfileById(profileId);
        if (userId == null) {
            profile.setUser(null);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Utilisateur introuvable"));
            if (profileRepository.findByUser_Id(userId).filter(p -> !p.getId().equals(profileId)).isPresent()) {
                throw new IllegalArgumentException("Cet utilisateur est déjà associé à un autre profil");
            }
            profile.setUser(user);
        }
        return profileRepository.save(profile);
    }

    public Profile getProfileById(Long id) {
        Profile profile = profileRepository.findByIdWithProfessionalProfiles(id)
                .orElseThrow(() -> new EntityNotFoundException("Ce profil n'existe pas!"));
        profileRepository.findByIdWithAcademicProfiles(id);
        return profile;
    }

    @Override
    public List<Profile> searchProfile(String keyword) {
        List<Profile> profiles;
        if (keyword == null || keyword.trim().isEmpty()) {
            profiles = profileRepository.findAllWithProfessionalProfiles();
        } else {
            profiles = profileRepository.findByKeywordWithProfessionalProfiles(keyword);
        }
        if (!profiles.isEmpty()) {
            profileRepository.findByIdInWithAcademicProfiles(
                    profiles.stream().map(Profile::getId).toList());
        }
        return profiles;
    }

    @Override
    public Profile update(Profile profile, User user) {
        if (!canEditProfile(profile, user)) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce profil");
        }
        return profileRepository.save(profile);
    }

    @Override
    public void delete(Long profileId, User currentUser) {
        Profile profile = getProfileById(profileId);
        if (!isOwnerOrAdmin(profile, currentUser)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to delete this profile");
        }
        profileRepository.delete(profile);
    }

    private boolean canEditProfile(Profile profile, User currentUser) {
        return isOwnerOrAdmin(profile, currentUser);
    }

    @Override
    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepository.findByUser_Id(userId)
                .map(profile -> getProfileById(profile.getId()));
    }

    @Override
    public boolean isOwnerOrAdmin(Profile profile, User currentUser) {
        if (currentUser.getRole() == UserRole.ADMIN) {
            return true;
        }
        return profile.getUser() != null && profile.getUser().getId().equals(currentUser.getId());
    }
}
