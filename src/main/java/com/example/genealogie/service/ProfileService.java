package com.example.genealogie.service;


import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;

import java.util.List;

public interface ProfileService {
    Profile create(Profile profile);
    Profile getProfileById(Long id);
    List<Profile> searchProfile(String keyword);
    Profile update(Profile profile, User user);
    void delete(Long profileId);
}
