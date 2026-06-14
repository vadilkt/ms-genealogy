package com.example.genealogie.service.impl;

import com.example.genealogie.dto.ValidationWarningDto;
import com.example.genealogie.model.Gender;
import com.example.genealogie.model.Marriage;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.ProfileService;
import com.example.genealogie.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private static final String BIRTH_AFTER_DEATH = "BIRTH_AFTER_DEATH";
    private static final String MARRIAGE_BEFORE_BIRTH = "MARRIAGE_BEFORE_BIRTH";
    private static final String MARRIAGE_AFTER_DEATH = "MARRIAGE_AFTER_DEATH";
    private static final String END_BEFORE_MARRIAGE = "END_BEFORE_MARRIAGE";
    private static final String CHILD_BORN_BEFORE_PARENT = "CHILD_BORN_BEFORE_PARENT";
    private static final String PARENT_TOO_YOUNG = "PARENT_TOO_YOUNG";
    private static final String MOTHER_DECEASED_BEFORE_BIRTH = "MOTHER_DECEASED_BEFORE_BIRTH";
    private static final String FATHER_DECEASED_BEFORE_CONCEPTION = "FATHER_DECEASED_BEFORE_CONCEPTION";

    /** Âge minimum plausible d'un parent à la naissance de son enfant (années). */
    private static final int MIN_PARENT_AGE_YEARS = 12;
    /** Tolérance de grossesse pour un père décédé avant la naissance (mois). */
    private static final int POSTHUMOUS_TOLERANCE_MONTHS = 9;

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
        Stream<Marriage> marriagesAsWife = profile.getMarriagesAsWife() != null
                ? profile.getMarriagesAsWife().stream()
                : Stream.empty();
        Stream.concat(marriagesAsHusband, marriagesAsWife).forEach(m -> addMarriageWarnings(m, warnings));

        Stream<Profile> children = Stream.concat(
                profileRepository.findByFather_Id(profile.getId()).stream(),
                profileRepository.findByMother_Id(profile.getId()).stream());
        children.forEach(child -> addParentChildWarnings(profile, child, warnings));

        return warnings;
    }

    private void addParentChildWarnings(Profile parent, Profile child, List<ValidationWarningDto> warnings) {
        String childName = child.getFirstName() + " " + child.getLastName();
        ZonedDateTime childBirth = child.getDateOfBirth();
        ZonedDateTime parentBirth = parent.getDateOfBirth();

        if (childBirth != null && parentBirth != null && childBirth.isBefore(parentBirth)) {
            warnings.add(new ValidationWarningDto(CHILD_BORN_BEFORE_PARENT,
                    "Un enfant (" + childName + ") est né avant ce parent."));
        } else if (childBirth != null && parentBirth != null
                && Period.between(parentBirth.toLocalDate(), childBirth.toLocalDate()).getYears() < MIN_PARENT_AGE_YEARS) {
            warnings.add(new ValidationWarningDto(PARENT_TOO_YOUNG,
                    "Ce parent avait moins de " + MIN_PARENT_AGE_YEARS
                            + " ans à la naissance de son enfant (" + childName + ")."));
        }

        ZonedDateTime parentDeath = parent.getDateOfDeath();
        if (childBirth != null && parentDeath != null) {
            if (parent.getGender() == Gender.FEMALE && parentDeath.isBefore(childBirth)) {
                warnings.add(new ValidationWarningDto(MOTHER_DECEASED_BEFORE_BIRTH,
                        "La mère était décédée avant la naissance de son enfant (" + childName + ")."));
            } else if (parent.getGender() == Gender.MALE
                    && parentDeath.plusMonths(POSTHUMOUS_TOLERANCE_MONTHS).isBefore(childBirth)) {
                warnings.add(new ValidationWarningDto(FATHER_DECEASED_BEFORE_CONCEPTION,
                        "Le père était décédé plus de " + POSTHUMOUS_TOLERANCE_MONTHS
                                + " mois avant la naissance de son enfant (" + childName + ")."));
            }
        }
    }

    private void addMarriageWarnings(Marriage marriage, List<ValidationWarningDto> warnings) {
        if (marriage.getEndDate() != null && marriage.getMarriageDate() != null
                && marriage.getEndDate().isBefore(marriage.getMarriageDate())) {
            warnings.add(new ValidationWarningDto(END_BEFORE_MARRIAGE,
                    "La date de fin du mariage est antérieure à la date de mariage."));
        }
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
