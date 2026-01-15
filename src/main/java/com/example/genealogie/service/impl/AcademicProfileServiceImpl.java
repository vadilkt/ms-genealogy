package com.example.genealogie.service.impl;

import com.example.genealogie.model.AcademicProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
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
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Profil académique avec l'id %s introuvable", id)));

        validateAccess(academicProfile.getProfile(), user);

        return academicProfile;
    }

    @Override
    public List<AcademicProfile> getAcademicExpByProfileId(Long id, User user) {
        validateAccess(profileService.getProfileById(id), user);
        return academicProfileRepository.findAllByProfile_Id(id, DEFAULT_SORT);
    }

    @Override
    public void delete(Long academicProfileId, User user) {
        AcademicProfile academicProfile = academicProfileRepository.findById(academicProfileId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Profil académique avec l'id %s introuvable", academicProfileId)));
        validateAccess(academicProfile.getProfile(), user);
        academicProfileRepository.delete(academicProfile);
    }

    private void validateAccess(Profile profile, User currentUser) {
        if (!profileService.isOwnerOrAdmin(profile, currentUser)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous n'êtes pas autorisé à accéder ou à modifier les données académiques de ce profil");
        }
    }
}
