package com.example.genealogie.service.impl;

import com.example.genealogie.dto.PlaceDto;
import com.example.genealogie.dto.PlaceRequestDto;
import com.example.genealogie.mapper.PlaceMapper;
import com.example.genealogie.model.Place;
import com.example.genealogie.repository.PlaceRepository;
import com.example.genealogie.service.PlaceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    @Override
    @Transactional
    public Place create(PlaceRequestDto dto) {
        Place place = Place.builder()
                .city(dto.getCity())
                .country(dto.getCountry())
                .region(dto.getRegion())
                .build();
        return placeRepository.save(place);
    }

    @Override
    @Transactional(readOnly = true)
    public Place getById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lieu non trouvé : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceDto> findAll() {
        return placeRepository.findAll().stream()
                .map(placeMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlaceDto findByIdAsDto(Long id) {
        return placeMapper.toDto(getById(id));
    }
}
