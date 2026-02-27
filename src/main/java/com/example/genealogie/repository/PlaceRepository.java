package com.example.genealogie.repository;

import com.example.genealogie.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByCityContainingIgnoreCaseOrCountryContainingIgnoreCase(String city, String country);
}
