package com.example.genealogie.service.impl;

import com.example.genealogie.model.ProfessionalProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.repository.ProfessionalProfileRepository;
import com.example.genealogie.service.ProfessionalProfileService;
import com.example.genealogie.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionalProfileServiceImpl implements ProfessionalProfileService {

    private final ProfessionalProfileRepository professionalProfileRepository;
    private final ProfileService profileService;
    private final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc(ProfessionalProfile.Fields.dateDebut));

    @Override
    public ProfessionalProfile getById(Long id, User user) {
        ProfessionalProfile professionalProfile = professionalProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Profil professionnel avec l'id %s introuvable", id)));
        validateAccess(professionalProfile.getProfile(), user);

        return professionalProfile;
    }

    @Override
    public ProfessionalProfile create(ProfessionalProfile professionalProfile, User user) {
        validateAccess(professionalProfile.getProfile(), user);
        return professionalProfileRepository.save(professionalProfile);
    }

    @Override
    public List<ProfessionalProfile> getAllByProfileId(Long id, User user) {
        validateAccess(profileService.getProfileById(id), user);
        return professionalProfileRepository.findAllByProfile_Id(id, DEFAULT_SORT);
    }

    @Override
    public ProfessionalProfile update(ProfessionalProfile professionalProfile, User user) {
        validateAccess(professionalProfile.getProfile(), user);
        return professionalProfileRepository.save(professionalProfile);
    }

    @Override
    public void delete(Long professionalProfileId, User user) {
        ProfessionalProfile professionalProfile = professionalProfileRepository.findById(professionalProfileId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Profil professionnel avec l'id %s introuvable", professionalProfileId)));
        validateAccess(professionalProfile.getProfile(), user);
        professionalProfileRepository.delete(professionalProfile);
    }

    private void validateAccess(Profile profile, User currentUser) {
        if (!profileService.isOwnerOrAdmin(profile, currentUser)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous n'êtes pas autorisé à accéder ou à modifier les données professionnelles de ce profil");
        }
    }
}
