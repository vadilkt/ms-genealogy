package com.example.genealogie.controller;

import com.example.genealogie.dto.ProfileRequestDto;
import com.example.genealogie.dto.ProfileResponseDto;
import com.example.genealogie.mapper.ProfileMapper;
import com.example.genealogie.model.Profile;
import com.example.genealogie.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileMapper profileMapper;
    private final ProfileService profileService;

    @PostMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> createProfile(@RequestBody ProfileRequestDto requestDto,
                                                            @PathVariable Long userId) {
        Profile profile = profileMapper.toEntity(requestDto, userId);
        profileService.create(profile);

        return ResponseEntity.ok().body(profileMapper.toDto(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long id) {
        Profile profile = profileService.getProfileById(id);

        return ResponseEntity.ok().body(profileMapper.toDto(profile));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProfileResponseDto>> search(@RequestParam(required = false) String keyword) {
        List<Profile> profiles = profileService.searchProfile(keyword);
        List<ProfileResponseDto> profileResponseDtos = profiles.stream()
                .map(profileMapper::toDto)
                .toList();

        return ResponseEntity.ok().body(profileResponseDtos);
    }
}
