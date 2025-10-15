package com.example.genealogie.controller;

import com.example.genealogie.dto.*;
import com.example.genealogie.mapper.AcademicProfileMapper;
import com.example.genealogie.mapper.ProfessionalProfileMapper;
import com.example.genealogie.mapper.ProfileMapper;
import com.example.genealogie.model.AcademicProfile;
import com.example.genealogie.model.ProfessionalProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.service.AcademicProfileService;
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
    private final AcademicProfileMapper academicProfileMapper;
    private final AcademicProfileService academicProfileService;

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

    @PutMapping("/professional/{professionalId}")
    public ResponseEntity<ProfessionalProfileResponseDto> update(@PathVariable Long professionalId,
                                                                 @RequestBody ProfessionalProfileRequestDto requestDto,
                                                                 @AuthenticationPrincipal User currentUser) {
        ProfessionalProfile existingProfessional = professionalProfileService.getById(professionalId, currentUser);
        ProfessionalProfile newProfessionalData = professionalProfileMapper.toEntity(requestDto, existingProfessional.getProfile().getId());
        professionalProfileMapper.update(existingProfessional, newProfessionalData);

        return ResponseEntity.ok(professionalProfileMapper.toDto(professionalProfileService.update(existingProfessional, currentUser)));
    }

    @DeleteMapping("/professional/{professionalId}")
    public ResponseEntity<Void> delete(@PathVariable Long professionalId, @AuthenticationPrincipal User currentUser) {
        professionalProfileService.delete(professionalId, currentUser);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/{id}/academic")
    public ResponseEntity<List<AcademicProfileResponseDto>> getAcademicExByProfileId(@PathVariable Long id,
                                                                                     @AuthenticationPrincipal User currentUser) {
        List<AcademicProfile> academicProfiles = academicProfileService.getAcademicExpByProfileId(id, currentUser);

        List<AcademicProfileResponseDto> dtos = academicProfiles.stream()
                .map(academicProfileMapper::toDto)
                .toList();

        return ResponseEntity.ok().body(dtos);
    }

    @PostMapping("/{id}/academic")
    public ResponseEntity<AcademicProfileResponseDto> createAcademic(@PathVariable Long id,
                                                                     @RequestBody AcademicProfileRequestDto dto,
                                                                     @AuthenticationPrincipal User currentUser) {
        AcademicProfile academicProfile = academicProfileMapper.toEntity(dto, id);
        academicProfileService.create(academicProfile, currentUser);

        return ResponseEntity.ok().body(academicProfileMapper.toDto(academicProfile));
    }

    @PutMapping("/academic/{id}")
    public ResponseEntity<AcademicProfileResponseDto> updateAcademic(@PathVariable Long id,
                                                                     @RequestBody AcademicProfileRequestDto dto,
                                                                     @AuthenticationPrincipal User currentUser) {
        AcademicProfile existingAcademicProfile = academicProfileService.getById(id, currentUser);
        AcademicProfile newAcademicProfile = academicProfileMapper.toEntity(dto, existingAcademicProfile.getProfile().getId());
        academicProfileMapper.update(existingAcademicProfile, newAcademicProfile);

        return ResponseEntity.ok().body(academicProfileMapper.toDto(academicProfileService.update(existingAcademicProfile, currentUser)));
    }

    @GetMapping("/academic/{id}")
    public ResponseEntity<AcademicProfileResponseDto> getAcademic(@PathVariable Long id,
                                                                  @AuthenticationPrincipal User currentUser) {
        AcademicProfile academicProfile = academicProfileService.getById(id, currentUser);

        return ResponseEntity.ok().body(academicProfileMapper.toDto(academicProfile));
    }

}
