package com.example.genealogie.controller;

import com.example.genealogie.dto.ProfessionalProfileRequestDto;
import com.example.genealogie.dto.ProfessionalProfileResponseDto;
import com.example.genealogie.dto.ProfileRequestDto;
import com.example.genealogie.dto.ProfileResponseDto;
import com.example.genealogie.mapper.ProfessionalProfileMapper;
import com.example.genealogie.mapper.ProfileMapper;
import com.example.genealogie.model.ProfessionalProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.service.ProfessionalProfileService;
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
    private final ProfessionalProfileMapper professionalProfileMapper;
    private final ProfessionalProfileService professionalProfileService;
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/professional")
    public ResponseEntity<ProfessionalProfileResponseDto> createProfessionalProfil(@PathVariable Long id,
                                                                                   @RequestBody ProfessionalProfileRequestDto requestDto,
                                                                                   @AuthenticationPrincipal User currentUser) {
        ProfessionalProfile professionalProfile = professionalProfileMapper.toEntity(requestDto, id);
        professionalProfileService.create(professionalProfile, currentUser);

        return ResponseEntity.ok().body(professionalProfileMapper.toDto(professionalProfile));
    }

    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<ProfessionalProfileResponseDto> getProfessionalProfil(@PathVariable Long professionalId,
                                                                                @AuthenticationPrincipal User currentUser) {
        ProfessionalProfile professionalProfile = professionalProfileService.getById(professionalId, currentUser);

        return ResponseEntity.ok().body(professionalProfileMapper.toDto(professionalProfile));
    }

    @GetMapping("/{id}/professional")
    public ResponseEntity<List<ProfessionalProfileResponseDto>> getProfessionalExById(@PathVariable Long id,
                                                                                      @AuthenticationPrincipal User currentUser) {
        List<ProfessionalProfile> professionalProfiles = professionalProfileService.getAllByProfileId(id, currentUser);

        List<ProfessionalProfileResponseDto> dtos = professionalProfiles.stream()
                .map(professionalProfileMapper::toDto)
                .toList();

        return ResponseEntity.ok().body(dtos);
    }

}
