package com.example.genealogie.service;

import com.example.genealogie.dto.ProfileNodeDto;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProfileService {
    Profile create(Profile profile, User currentUser);

    Profile getProfileById(Long id);

    List<Profile> searchProfile(String keyword);

    Page<Profile> searchProfile(String keyword, Pageable pageable);

    Profile update(Profile profile, User user);

    void delete(Long profileId, User user);

    boolean isOwnerOrAdmin(Profile profile, User user);

    Optional<Profile> getProfileByUserId(Long userId);

    Profile assignUser(Long profileId, Long userId);

    Profile createEmpty(User user);

    List<Profile> getOrphanProfiles();

    List<ProfileNodeDto> getFamilyGraph();
}
