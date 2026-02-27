package com.example.genealogie.repository;

import com.example.genealogie.model.Marriage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarriageRepository extends JpaRepository<Marriage, Long> {

    List<Marriage> findByHusband_Id(Long husbandId);

    Optional<Marriage> findByWife_Id(Long wifeId);

    boolean existsByWife_Id(Long wifeId);
}
