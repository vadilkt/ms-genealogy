package com.example.genealogie.service.impl;

import com.example.genealogie.model.AcademicProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.repository.AcademicProfileRepository;
import com.example.genealogie.service.AcademicProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AcademicProfileServiceImpl implements AcademicProfileService {

    private final AcademicProfileRepository academicProfileRepository;

    @Override
    public AcademicProfile create(AcademicProfile academicProfile, User user) {
        validateAccess(academicProfile.getProfile(), user);
        return academicProfileRepository.save(academicProfile);
    }

    private boolean isCurrentUser(Profile profile, User currentUser){
        if(currentUser.getRole() == UserRole.ADMIN) return true;

        return profile.getUser().getId().equals(currentUser.getId());
    }

    private void validateAccess(Profile profile, User currentUser){
        if(!isCurrentUser(profile, currentUser)) {
            throw new SecurityException("You are not authorized to create a professional profile");
        }
    }
}
