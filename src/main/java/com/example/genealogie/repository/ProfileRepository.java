package com.example.genealogie.repository;

import com.example.genealogie.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByUser_Id(Long userId);

    @Query("SELECT p FROM Profile p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.residence) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Profile> findByKeyword(@Param("keyword") String keyword);
}
