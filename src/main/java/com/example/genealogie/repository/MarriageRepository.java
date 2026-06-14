package com.example.genealogie.repository;

import com.example.genealogie.model.Marriage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarriageRepository extends JpaRepository<Marriage, Long> {

    List<Marriage> findByHusband_Id(Long husbandId);

    /** Tous les mariages d'une femme (historique inclus), du plus ancien au plus récent. */
    List<Marriage> findByWife_IdOrderByMarriageDateAsc(Long wifeId);

    /** Vrai si la femme a un mariage <em>actif</em> (sans date de fin). */
    boolean existsByWife_IdAndEndDateIsNull(Long wifeId);
}
