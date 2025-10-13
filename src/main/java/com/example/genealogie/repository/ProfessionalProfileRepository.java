package com.example.genealogie.repository;

import com.example.genealogie.model.ProfessionalProfile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessionalProfileRepository extends JpaRepository<ProfessionalProfile, Long> {
    List<ProfessionalProfile> findAllByProfile_Id(Long profileId, Sort sort);
}
