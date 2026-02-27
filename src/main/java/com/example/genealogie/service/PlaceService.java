package com.example.genealogie.service;

import com.example.genealogie.dto.PlaceDto;
import com.example.genealogie.dto.PlaceRequestDto;
import com.example.genealogie.model.Place;

import java.util.List;

public interface PlaceService {

    Place create(PlaceRequestDto dto);

    Place getById(Long id);

    List<PlaceDto> findAll();

    PlaceDto findByIdAsDto(Long id);
}
