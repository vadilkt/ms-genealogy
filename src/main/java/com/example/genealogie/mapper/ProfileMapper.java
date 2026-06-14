package com.example.genealogie.mapper;

import com.example.genealogie.dto.AcademicProfileResponseDto;
import com.example.genealogie.dto.PlaceDto;
import com.example.genealogie.dto.ProfileRequestDto;
import com.example.genealogie.dto.ProfileResponseDto;
import com.example.genealogie.dto.ProfessionalProfileResponseDto;
import com.example.genealogie.model.DatePrecision;
import com.example.genealogie.model.DateQualifier;
import com.example.genealogie.model.Gender;
import com.example.genealogie.model.Place;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.repository.PlaceRepository;
import com.example.genealogie.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public abstract class ProfileMapper {

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceMapper placeMapper;

    @Autowired
    private ProfessionalProfileMapper professionalProfileMapper;

    @Autowired
    private AcademicProfileMapper academicProfileMapper;

    @Autowired
    private com.example.genealogie.service.ProfileService profileService;

    public ProfileResponseDto toDto(Profile profile, User currentUser) {
        if (profile == null) {
            return null;
        }

        ProfileResponseDto dto = new ProfileResponseDto();

        dto.setId(profile.getId());
        dto.setUserId(profile.getUser() != null ? profile.getUser().getId() : null);
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setGender(profile.getGender() != null ? profile.getGender().name() : null);
        dto.setResidence(profile.getResidence());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setDateOfDeath(profile.getDateOfDeath());
        applyDateMeta(dto, profile);
        dto.setAge(computeAge(profile.getDateOfBirth(), profile.getDateOfDeath()));
        dto.setBirthPlace(placeMapper.toDto(profile.getBirthPlace()));
        dto.setDeathPlace(placeMapper.toDto(profile.getDeathPlace()));
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());

        // Profession : donnée publique, visible par tous les utilisateurs.
        List<ProfessionalProfileResponseDto> professionalDtos = profile.getProfessionalProfiles() == null
                ? Collections.emptyList()
                : profile.getProfessionalProfiles()
                        .stream()
                        .map(professionalProfileMapper::toDto)
                        .toList();
        dto.setProfessionalRecords(professionalDtos);

        // Parcours académique : visible par le propriétaire/admin, ou pour une personne décédée.
        boolean canSeeAcademic = (currentUser != null && profileService.isOwnerOrAdmin(profile, currentUser))
                || profile.getDateOfDeath() != null;
        List<AcademicProfileResponseDto> academicProfileResponseDtos = (canSeeAcademic
                && profile.getAcademicProfiles() != null)
                        ? profile.getAcademicProfiles().stream()
                                .map(academicProfileMapper::toDto)
                                .toList()
                        : Collections.emptyList();
        dto.setAcademicRecords(academicProfileResponseDtos);

        return dto;
    }

    /**
     * Version légère pour les membres de la famille (père, mère, enfants, époux)
     * sans charger les listes professionnelles/académiques.
     */
    public ProfileResponseDto toDtoSummary(Profile profile) {
        if (profile == null) {
            return null;
        }
        ProfileResponseDto dto = new ProfileResponseDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser() != null ? profile.getUser().getId() : null);
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setGender(profile.getGender() != null ? profile.getGender().name() : null);
        dto.setResidence(profile.getResidence());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setDateOfDeath(profile.getDateOfDeath());
        applyDateMeta(dto, profile);
        dto.setAge(computeAge(profile.getDateOfBirth(), profile.getDateOfDeath()));
        dto.setBirthPlace(placeMapper.toDto(profile.getBirthPlace()));
        dto.setDeathPlace(placeMapper.toDto(profile.getDeathPlace()));
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        dto.setProfessionalRecords(Collections.emptyList());
        dto.setAcademicRecords(Collections.emptyList());
        return dto;
    }

    /** Copie les qualificatifs/précisions de dates (avec valeurs par défaut) vers le DTO. */
    private void applyDateMeta(ProfileResponseDto dto, Profile profile) {
        dto.setBirthDateQualifier((profile.getBirthDateQualifier() != null
                ? profile.getBirthDateQualifier() : DateQualifier.EXACT).name());
        dto.setBirthDatePrecision((profile.getBirthDatePrecision() != null
                ? profile.getBirthDatePrecision() : DatePrecision.DAY).name());
        dto.setDeathDateQualifier((profile.getDeathDateQualifier() != null
                ? profile.getDeathDateQualifier() : DateQualifier.EXACT).name());
        dto.setDeathDatePrecision((profile.getDeathDatePrecision() != null
                ? profile.getDeathDatePrecision() : DatePrecision.DAY).name());
    }

    private DateQualifier parseQualifier(String value) {
        if (value == null) return DateQualifier.EXACT;
        try {
            return DateQualifier.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DateQualifier.EXACT;
        }
    }

    private DatePrecision parsePrecision(String value) {
        if (value == null) return DatePrecision.DAY;
        try {
            return DatePrecision.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DatePrecision.DAY;
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "birthPlace", ignore = true)
    @Mapping(target = "deathPlace", ignore = true)
    public abstract Profile update(@MappingTarget Profile target, Profile source);

    public Profile toEntity(ProfileRequestDto requestDto, Long userId) {
        if (requestDto == null) {
            return null;
        }

        Profile profile = new Profile();

        profile.setFirstName(requestDto.getFirstName());
        profile.setLastName(requestDto.getLastName());
        profile.setResidence(requestDto.getResidence());
        profile.setDateOfBirth(requestDto.getDateOfBirth());
        profile.setDateOfDeath(requestDto.getDateOfDeath());
        profile.setBirthDateQualifier(parseQualifier(requestDto.getBirthDateQualifier()));
        profile.setBirthDatePrecision(parsePrecision(requestDto.getBirthDatePrecision()));
        profile.setDeathDateQualifier(parseQualifier(requestDto.getDeathDateQualifier()));
        profile.setDeathDatePrecision(parsePrecision(requestDto.getDeathDatePrecision()));
        profile.setGender(getGender(requestDto.getGender()));
        profile.setBirthPlace(requestDto.getBirthPlaceId() != null
                ? placeRepository.findById(requestDto.getBirthPlaceId()).orElse(null)
                : null);
        profile.setDeathPlace(requestDto.getDeathPlaceId() != null
                ? placeRepository.findById(requestDto.getDeathPlaceId()).orElse(null)
                : null);

        if (userId != null) {
            User user = getUserById(userId);
            profile.setUser(user);
        }

        return profile;
    }

    @Named("stringToGender")
    private Gender getGender(String gender) {
        if (gender == null) {
            return null;
        }

        return switch (gender.toLowerCase()) {
            case "male" -> Gender.MALE;
            case "female" -> Gender.FEMALE;
            default -> null;
        };
    }

    @Named("getUserById")
    private User getUserById(Long id) {
        return userService.getUserById(id);
    }

    private Integer computeAge(@Nullable ZonedDateTime birthDate, @Nullable ZonedDateTime deathDate) {
        if (birthDate == null) {
            return null;
        }
        ZonedDateTime end = Objects.requireNonNullElseGet(deathDate, ZonedDateTime::now);
        if (end.isBefore(birthDate)) {
            return 0;
        }
        return Period.between(birthDate.toLocalDate(), end.toLocalDate()).getYears();
    }
}