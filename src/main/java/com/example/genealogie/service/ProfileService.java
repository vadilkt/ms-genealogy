package com.example.genealogie.service;

import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;

import java.util.List;
import java.util.Optional;

public interface ProfileService {
    Profile create(Profile profile, User currentUser);

    Profile getProfileById(Long id);

    List<Profile> searchProfile(String keyword);

    Profile update(Profile profile, User user);

    void delete(Long profileId, User user);

    boolean isOwnerOrAdmin(Profile profile, User user);

    Optional<Profile> getProfileByUserId(Long userId);

    Profile assignUser(Long profileId, Long userId);
}
