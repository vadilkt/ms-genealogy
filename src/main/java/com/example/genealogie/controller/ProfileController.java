package com.example.genealogie.controller;

import com.example.genealogie.dto.*;
import com.example.genealogie.mapper.AcademicProfileMapper;
import com.example.genealogie.mapper.ProfessionalProfileMapper;
import com.example.genealogie.mapper.ProfileMapper;
import com.example.genealogie.model.AcademicProfile;
import com.example.genealogie.model.ProfessionalProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.service.AcademicProfileService;
import com.example.genealogie.service.FamilyService;
import com.example.genealogie.service.PlaceService;
import com.example.genealogie.service.ProfessionalProfileService;
import com.example.genealogie.service.ProfileService;
import com.example.genealogie.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
    private final FamilyService familyService;
    private final PlaceService placeService;
    private final ValidationService validationService;

    @PostMapping
    public ResponseEntity<ProfileResponseDto> createProfile(@RequestBody @Valid ProfileRequestDto requestDto,
            @AuthenticationPrincipal User currentUser) {
        // Admin: no auto-assign (profile stays unlinked until explicitly assigned)
        // User: links profile to their own account
        Long userId = currentUser.getRole() == UserRole.ADMIN ? null : currentUser.getId();
        Profile profile = profileMapper.toEntity(requestDto, userId);
        profile = profileService.create(profile, currentUser);
        return ResponseEntity.ok().body(profileMapper.toDto(profile, currentUser));
    }

    @PutMapping("/{id}/user")
    public ResponseEntity<ProfileResponseDto> assignUser(@PathVariable Long id,
            @RequestBody AssignUserRequestDto requestDto,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        Profile profile = profileService.assignUser(id, requestDto.getUserId());
        return ResponseEntity.ok(profileMapper.toDto(profile, currentUser));
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDto> getMyProfile(@AuthenticationPrincipal User currentUser) {
        return profileService.getProfileByUserId(currentUser.getId())
                .map(profile -> ResponseEntity.ok(profileMapper.toDto(profile, currentUser)))
                .orElse(ResponseEntity.noContent().<ProfileResponseDto>build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        Profile profile = profileService.getProfileById(id);

        return ResponseEntity.ok().body(profileMapper.toDto(profile, currentUser));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProfileResponseDto>> search(@RequestParam(required = false) String keyword,
            @AuthenticationPrincipal User currentUser) {
        List<Profile> profiles = profileService.searchProfile(keyword);
        List<ProfileResponseDto> profileResponseDtos = profiles.stream()
                .map(p -> profileMapper.toDto(p, currentUser))
                .toList();

        return ResponseEntity.ok().body(profileResponseDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> update(@PathVariable Long id,
            @RequestBody @Valid ProfileRequestDto requestDto,
            @AuthenticationPrincipal User currentUser) {
        Profile existingProfile = profileService.getProfileById(id);

        Profile newProfileData = profileMapper.toEntity(requestDto, currentUser.getId());
        profileMapper.update(existingProfile, newProfileData);
        existingProfile.setBirthPlace(
                requestDto.getBirthPlaceId() != null ? placeService.getById(requestDto.getBirthPlaceId()) : null);
        existingProfile.setDeathPlace(
                requestDto.getDeathPlaceId() != null ? placeService.getById(requestDto.getDeathPlaceId()) : null);

        Profile updated = profileService.update(existingProfile, currentUser);
        return ResponseEntity.ok(profileMapper.toDto(updated, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        profileService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/professional")
    public ResponseEntity<ProfessionalProfileResponseDto> createProfessionalProfil(@PathVariable Long id,
            @RequestBody @Valid ProfessionalProfileRequestDto requestDto,
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
            @RequestBody @Valid ProfessionalProfileRequestDto requestDto,
            @AuthenticationPrincipal User currentUser) {
        ProfessionalProfile existingProfessional = professionalProfileService.getById(professionalId, currentUser);
        ProfessionalProfile newProfessionalData = professionalProfileMapper.toEntity(requestDto,
                existingProfessional.getProfile().getId());
        professionalProfileMapper.update(existingProfessional, newProfessionalData);

        return ResponseEntity.ok(
                professionalProfileMapper.toDto(professionalProfileService.update(existingProfessional, currentUser)));
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
            @RequestBody @Valid AcademicProfileRequestDto dto,
            @AuthenticationPrincipal User currentUser) {
        AcademicProfile academicProfile = academicProfileMapper.toEntity(dto, id);
        academicProfileService.create(academicProfile, currentUser);

        return ResponseEntity.ok().body(academicProfileMapper.toDto(academicProfile));
    }

    @PutMapping("/academic/{id}")
    public ResponseEntity<AcademicProfileResponseDto> updateAcademic(@PathVariable Long id,
            @RequestBody @Valid AcademicProfileRequestDto dto,
            @AuthenticationPrincipal User currentUser) {
        AcademicProfile existingAcademicProfile = academicProfileService.getById(id, currentUser);
        AcademicProfile newAcademicProfile = academicProfileMapper.toEntity(dto,
                existingAcademicProfile.getProfile().getId());
        academicProfileMapper.update(existingAcademicProfile, newAcademicProfile);

        return ResponseEntity.ok()
                .body(academicProfileMapper.toDto(academicProfileService.update(existingAcademicProfile, currentUser)));
    }

    @GetMapping("/academic/{id}")
    public ResponseEntity<AcademicProfileResponseDto> getAcademic(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        AcademicProfile academicProfile = academicProfileService.getById(id, currentUser);

        return ResponseEntity.ok().body(academicProfileMapper.toDto(academicProfile));
    }

    @DeleteMapping("/academic/{id}")
    public ResponseEntity<Void> deleteAcademic(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        academicProfileService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // --- Liens familiaux (arbre généalogique) ---

    @GetMapping("/{id}/family")
    public ResponseEntity<FamilyResponseDto> getFamily(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.getFamily(id, currentUser));
    }

    /**
     * Arbre des ancêtres : parents, grands-parents, arrière-grands-parents, etc.
     * 
     * @param depth profondeur (1 = parents, 2 = + grands-parents, ...). Défaut 5,
     *              max 10.
     */
    @GetMapping("/{id}/ancestors")
    public ResponseEntity<AncestorNodeDto> getAncestors(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "5") int depth,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.getAncestors(id, depth, currentUser));
    }

    /**
     * Arbre des descendants : enfants, petits-enfants, etc.
     * 
     * @param depth profondeur (1 = enfants, 2 = + petits-enfants, ...). Défaut 5,
     *              max 10.
     */
    @GetMapping("/{id}/descendants")
    public ResponseEntity<DescendantNodeDto> getDescendants(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "5") int depth,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.getDescendants(id, depth, currentUser));
    }

    /**
     * Avertissements de cohérence sur le profil (dates incohérentes, mariage hors
     * vie, etc.).
     */
    @GetMapping("/{id}/warnings")
    public ResponseEntity<List<ValidationWarningDto>> getProfileWarnings(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(validationService.getProfileWarnings(id, currentUser));
    }

    @GetMapping("/{id}/parents")
    public ResponseEntity<FamilyResponseDto> getParents(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.getParents(id, currentUser));
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<ProfileResponseDto>> getChildren(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.getChildren(id, currentUser));
    }

    @GetMapping("/{id}/spouses")
    public ResponseEntity<List<ProfileResponseDto>> getSpouses(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.getSpouses(id, currentUser));
    }

    /**
     * Frères et sœurs : profils ayant au moins un parent en commun (père ou mère).
     */
    @GetMapping("/{id}/siblings")
    public ResponseEntity<List<ProfileResponseDto>> getSiblings(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.getSiblings(id, currentUser));
    }

    @PutMapping("/{id}/father")
    public ResponseEntity<ProfileResponseDto> setFather(@PathVariable Long id,
            @RequestBody @Valid SetParentRequestDto request,
            @AuthenticationPrincipal User currentUser) {
        Profile profile = familyService.setFather(id, request.getParentProfileId(), currentUser);
        return ResponseEntity.ok(profileMapper.toDtoSummary(profile));
    }

    @PutMapping("/{id}/mother")
    public ResponseEntity<ProfileResponseDto> setMother(@PathVariable Long id,
            @RequestBody @Valid SetParentRequestDto request,
            @AuthenticationPrincipal User currentUser) {
        Profile profile = familyService.setMother(id, request.getParentProfileId(), currentUser);
        return ResponseEntity.ok(profileMapper.toDtoSummary(profile));
    }

    @DeleteMapping("/{id}/father")
    public ResponseEntity<Void> removeFather(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        familyService.removeFather(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/mother")
    public ResponseEntity<Void> removeMother(@PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        familyService.removeMother(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/marriage")
    public ResponseEntity<MarriageResponseDto> addMarriage(@PathVariable Long id,
            @RequestBody @Valid CreateMarriageRequestDto request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.addMarriage(id, request, currentUser));
    }

    @DeleteMapping("/marriage/{marriageId}")
    public ResponseEntity<Void> removeMarriage(@PathVariable Long marriageId,
            @AuthenticationPrincipal User currentUser) {
        familyService.removeMarriage(marriageId, currentUser);
        return ResponseEntity.noContent().build();
    }

}
