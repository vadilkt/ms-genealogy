package com.example.genealogie.repository;

import com.example.genealogie.entities.ExperienceProfessionnelle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceProfessionnelRepository extends JpaRepository<ExperienceProfessionnelle, Long> {
    List<ExperienceProfessionnelle> findByProfileId(Long profileId);
}
