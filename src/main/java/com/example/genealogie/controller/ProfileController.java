package com.example.genealogie.controller;

import com.example.genealogie.dto.ProfileRequestDto;
import com.example.genealogie.dto.ProfileResponseDto;
import com.example.genealogie.mapper.ProfileMapper;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> update(@PathVariable Long id,
                                                     @RequestBody ProfileRequestDto requestDto,
                                                     @AuthenticationPrincipal User currentUser) {
        Profile existingProfile = profileService.getProfileById(id);

        Profile newProfileData = profileMapper.toEntity(requestDto, currentUser.getId());
        profileMapper.update(existingProfile, newProfileData);

        Profile updated = profileService.update(existingProfile, currentUser);
        return ResponseEntity.ok(profileMapper.toDto(updated));
    }

}
