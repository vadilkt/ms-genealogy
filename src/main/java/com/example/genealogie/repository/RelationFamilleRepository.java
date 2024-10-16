package com.example.genealogie.repository;

import com.example.genealogie.entities.RelationFamille;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelationFamilleRepository extends JpaRepository<RelationFamille, Long> {
    List<RelationFamille> findByProfileId(Long id);
    List<RelationFamille> findByRelativeId(Long id);
}
