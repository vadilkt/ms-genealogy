package com.example.genealogie.controller;

import com.example.genealogie.dto.PlaceDto;
import com.example.genealogie.dto.PlaceRequestDto;
import com.example.genealogie.model.Place;
import com.example.genealogie.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping
    public ResponseEntity<PlaceDto> create(@RequestBody @Valid PlaceRequestDto dto,
                                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {
        Place place = placeService.create(dto);
        PlaceDto result = placeService.findByIdAsDto(place.getId());
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(place.getId()).toUri()
        ).body(result);
    }

    @GetMapping
    public ResponseEntity<List<PlaceDto>> findAll(@AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {
        return ResponseEntity.ok(placeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceDto> getById(@PathVariable Long id,
                                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {
        return ResponseEntity.ok(placeService.findByIdAsDto(id));
    }
}
