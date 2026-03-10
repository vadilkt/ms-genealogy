package com.example.genealogie.repository;

import com.example.genealogie.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByUser_Id(Long userId);

    java.util.Optional<Profile> findByUser_Id(Long userId);

    /** Une seule collection en JOIN FETCH pour éviter MultipleBagFetchException. */
    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.professionalProfiles WHERE p.id = :id")
    java.util.Optional<Profile> findByIdWithProfessionalProfiles(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.academicProfiles WHERE p.id = :id")
    java.util.Optional<Profile> findByIdWithAcademicProfiles(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.professionalProfiles")
    List<Profile> findAllWithProfessionalProfiles();

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.academicProfiles WHERE p.id IN :ids")
    List<Profile> findByIdInWithAcademicProfiles(@Param("ids") Collection<Long> ids);

    @Query("SELECT p FROM Profile p LEFT JOIN p.birthPlace bp WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.residence) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(bp.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(bp.country) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Profile> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.professionalProfiles LEFT JOIN p.birthPlace bp WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.residence) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(bp.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(bp.country) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Profile> findByKeywordWithProfessionalProfiles(@Param("keyword") String keyword);

    List<Profile> findByFather_Id(Long fatherId);

    List<Profile> findByMother_Id(Long motherId);

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.professionalProfiles WHERE p.user IS NULL")
    List<Profile> findOrphansWithProfessionalProfiles();

    /** Charge tous les profils avec père/mère pour le graphe familial global. */
    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.father LEFT JOIN FETCH p.mother")
    List<Profile> findAllWithParents();
}
