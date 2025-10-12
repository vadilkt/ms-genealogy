package com.example.genealogie.service;

import com.example.genealogie.model.ProfessionalProfile;

public interface ProfessionalProfileService {
    ProfessionalProfile getById(Long id);
    ProfessionalProfile create(ProfessionalProfile professionalProfile);
}
