package com.example.genealogie.service;

import com.example.genealogie.model.AcademicProfile;
import com.example.genealogie.model.User;

public interface AcademicProfileService {
    AcademicProfile create(AcademicProfile academicProfile, User user);
    AcademicProfile update(AcademicProfile academicProfile, User user);
    AcademicProfile getById(Long id, User user);
}
