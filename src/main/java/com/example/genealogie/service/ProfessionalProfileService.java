package com.example.genealogie.service;

import com.example.genealogie.model.ProfessionalProfile;
import com.example.genealogie.model.User;

import java.util.List;

public interface ProfessionalProfileService {
    ProfessionalProfile getById(Long id, User user);
    ProfessionalProfile create(ProfessionalProfile professionalProfile, User user);
    List<ProfessionalProfile> getAllByProfileId(Long id, User user);
    ProfessionalProfile update(ProfessionalProfile professionalProfile, User user);
    void delete(Long professionalProfileId, User user);
}
