package com.example.genealogie.service.impl;

import com.example.genealogie.model.AcademicProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.repository.AcademicProfileRepository;
import com.example.genealogie.service.AcademicProfileService;
import com.example.genealogie.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicProfileServiceImpl implements AcademicProfileService {

    private final ProfileService profileService;
    private final AcademicProfileRepository academicProfileRepository;
    private final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc(AcademicProfile.Fields.startDate));

    @Override
    public AcademicProfile create(AcademicProfile academicProfile, User user) {
        validateAccess(academicProfile.getProfile(), user);
        return academicProfileRepository.save(academicProfile);
    }

    @Override
    public AcademicProfile update(AcademicProfile academicProfile, User user) {
        validateAccess(academicProfile.getProfile(), user);
        return academicProfileRepository.save(academicProfile);
    }

    @Override
    public AcademicProfile getById(Long id, User user) {
        AcademicProfile academicProfile = academicProfileRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Academic profile with id %s not found", id)));

        validateAccess(academicProfile.getProfile(), user);

        return academicProfile;
    }

    @Override
    public List<AcademicProfile> getAcademicExpByProfileId(Long id, User user) {
        validateAccess(profileService.getProfileById(id), user);
        return academicProfileRepository.findAllByProfile_Id(id, DEFAULT_SORT);
    }

    @Override
    public void delete(Long profileId, User user) {
        validateAccess(profileService.getProfileById(profileId), user);
        AcademicProfile academicProfile = getById(profileId, user);
        academicProfileRepository.delete(academicProfile);
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
