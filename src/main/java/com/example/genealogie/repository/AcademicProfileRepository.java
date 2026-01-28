package com.example.genealogie.repository;

import com.example.genealogie.model.AcademicProfile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicProfileRepository extends JpaRepository<AcademicProfile, Long> {
    List<AcademicProfile> findAllByProfile_Id(Long profileId, Sort sort);
}
