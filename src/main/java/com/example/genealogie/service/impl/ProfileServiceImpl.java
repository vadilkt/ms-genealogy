package com.example.genealogie.service.impl;

import com.example.genealogie.model.Profile;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
