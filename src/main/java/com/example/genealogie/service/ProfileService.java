package com.example.genealogie.service;


import com.example.genealogie.model.Profile;

import java.util.List;

public interface ProfileService {
    Profile create(Profile profile);
    Profile getProfileById(Long id);
    List<Profile> searchProfile(String keyword);
}
