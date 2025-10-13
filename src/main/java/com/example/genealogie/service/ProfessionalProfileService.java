package com.example.genealogie.service;

import com.example.genealogie.model.ProfessionalProfile;
import com.example.genealogie.model.User;

public interface ProfessionalProfileService {
    ProfessionalProfile getById(Long id, User user);
    ProfessionalProfile create(ProfessionalProfile professionalProfile, User user);
}
