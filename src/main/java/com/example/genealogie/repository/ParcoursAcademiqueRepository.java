package com.example.genealogie.repository;

import com.example.genealogie.entities.ParcoursAcademique;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParcoursAcademiqueRepository extends JpaRepository<ParcoursAcademique, Long> {
    List<ParcoursAcademique> findByProfileId(Long profileId);
}
