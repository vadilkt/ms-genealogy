package com.example.genealogie.service.impl;

import com.example.genealogie.model.ProfessionalProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.repository.ProfessionalProfileRepository;
import com.example.genealogie.service.ProfessionalProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfessionalProfileServiceImpl implements ProfessionalProfileService {

    private final ProfessionalProfileRepository professionalProfileRepository;

    @Override
    public ProfessionalProfile getById(Long id, User user) {
        ProfessionalProfile professionalProfile = professionalProfileRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Professional profile with id %s not found", id)));
        if(!isCurrentUser(professionalProfile.getProfile(), user)) {
            throw new SecurityException("You do not have permission to access this resource");
        }

        return professionalProfile;
    }

    @Override
    public ProfessionalProfile create(ProfessionalProfile professionalProfile, User user) {
        if(!isCurrentUser(professionalProfile.getProfile(), user)) {
            throw new SecurityException("You are not authorized to create a professional profile");
        }
        return professionalProfileRepository.save(professionalProfile);
    }

    private boolean isCurrentUser(Profile profile, User currentUser){
        if(currentUser.getRole() == UserRole.ADMIN) return true;

        return profile.getUser().getId().equals(currentUser.getId());
    }
}
