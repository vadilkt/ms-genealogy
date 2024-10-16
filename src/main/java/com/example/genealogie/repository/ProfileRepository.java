package com.example.genealogie.repository;

import com.example.genealogie.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByNomOrPrenom(String nom, String Prenom);
}
