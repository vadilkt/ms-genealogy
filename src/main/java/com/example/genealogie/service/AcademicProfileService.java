package com.example.genealogie.service;

import com.example.genealogie.model.AcademicProfile;
import com.example.genealogie.model.User;

import java.util.List;

public interface AcademicProfileService {
    AcademicProfile create(AcademicProfile academicProfile, User user);
    AcademicProfile update(AcademicProfile academicProfile, User user);
    AcademicProfile getById(Long id, User user);
    List<AcademicProfile> getAcademicExpByProfileId(Long id, User user);
    void delete(Long academicProfileId, User user);
}
