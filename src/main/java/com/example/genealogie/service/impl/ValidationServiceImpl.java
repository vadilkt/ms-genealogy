package com.example.genealogie.service.impl;

import com.example.genealogie.dto.ValidationWarningDto;
import com.example.genealogie.model.Marriage;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.ProfileService;
import com.example.genealogie.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private static final String BIRTH_AFTER_DEATH = "BIRTH_AFTER_DEATH";
    private static final String MARRIAGE_BEFORE_BIRTH = "MARRIAGE_BEFORE_BIRTH";
    private static final String MARRIAGE_AFTER_DEATH = "MARRIAGE_AFTER_DEATH";
    private static final String CHILD_BORN_BEFORE_PARENT = "CHILD_BORN_BEFORE_PARENT";

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ValidationWarningDto> getProfileWarnings(Long profileId, User currentUser) {
        Profile profile = profileService.getProfileById(profileId);
        List<ValidationWarningDto> warnings = new ArrayList<>();

        if (profile.getDateOfBirth() != null && profile.getDateOfDeath() != null
                && profile.getDateOfBirth().isAfter(profile.getDateOfDeath())) {
            warnings.add(new ValidationWarningDto(BIRTH_AFTER_DEATH,
                    "La date de naissance est postérieure à la date de décès."));
        }

        Stream<Marriage> marriagesAsHusband = profile.getMarriagesAsHusband() != null
                ? profile.getMarriagesAsHusband().stream()
                : Stream.empty();
        Stream<Marriage> marriageAsWife = profile.getMarriageAsWife() != null
                ? Stream.of(profile.getMarriageAsWife())
                : Stream.empty();
        Stream.concat(marriagesAsHusband, marriageAsWife).forEach(m -> addMarriageWarnings(m, profile, warnings));

        Stream<Profile> children = Stream.concat(
                profileRepository.findByFather_Id(profile.getId()).stream(),
                profileRepository.findByMother_Id(profile.getId()).stream());
        children.forEach(child -> {
            if (child.getDateOfBirth() != null && profile.getDateOfBirth() != null
                    && child.getDateOfBirth().isBefore(profile.getDateOfBirth())) {
                warnings.add(new ValidationWarningDto(CHILD_BORN_BEFORE_PARENT,
                        "Un enfant (" + child.getFirstName() + " " + child.getLastName() + ") est né avant ce parent."));
            }
        });

        return warnings;
    }

    private void addMarriageWarnings(Marriage marriage, Profile profile, List<ValidationWarningDto> warnings) {
        if (marriage.getMarriageDate() == null) return;
        Profile husband = marriage.getHusband();
        Profile wife = marriage.getWife();
        if (husband.getDateOfBirth() != null && marriage.getMarriageDate().isBefore(husband.getDateOfBirth())) {
            warnings.add(new ValidationWarningDto(MARRIAGE_BEFORE_BIRTH,
                    "La date du mariage est antérieure à la naissance du mari."));
        }
        if (husband.getDateOfDeath() != null && marriage.getMarriageDate().isAfter(husband.getDateOfDeath())) {
            warnings.add(new ValidationWarningDto(MARRIAGE_AFTER_DEATH,
                    "La date du mariage est postérieure au décès du mari."));
        }
        if (wife.getDateOfBirth() != null && marriage.getMarriageDate().isBefore(wife.getDateOfBirth())) {
            warnings.add(new ValidationWarningDto(MARRIAGE_BEFORE_BIRTH,
                    "La date du mariage est antérieure à la naissance de l'épouse."));
        }
        if (wife.getDateOfDeath() != null && marriage.getMarriageDate().isAfter(wife.getDateOfDeath())) {
            warnings.add(new ValidationWarningDto(MARRIAGE_AFTER_DEATH,
                    "La date du mariage est postérieure au décès de l'épouse."));
        }
    }
}
