package com.example.genealogie.service;

import com.example.genealogie.dto.ValidationWarningDto;
import com.example.genealogie.model.Gender;
import com.example.genealogie.model.Marriage;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.impl.ValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ValidationServiceImpl validationService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("password");
        adminUser.setRole(UserRole.ADMIN);
    }

    private Profile buildProfile(Long id, ZonedDateTime dob, ZonedDateTime dod, Gender gender) {
        Profile p = new Profile();
        p.setId(id);
        p.setFirstName("Test");
        p.setLastName("Profile" + id);
        p.setDateOfBirth(dob);
        p.setDateOfDeath(dod);
        p.setGender(gender);
        p.setMarriagesAsHusband(new ArrayList<>());
        return p;
    }

    @Test
    void noWarnings_whenDatesAreConsistent() {
        ZonedDateTime dob = ZonedDateTime.now().minusYears(40);
        ZonedDateTime dod = ZonedDateTime.now().minusYears(10);
        Profile profile = buildProfile(1L, dob, dod, Gender.MALE);

        when(profileService.getProfileById(1L)).thenReturn(profile);
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of());
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of());

        List<ValidationWarningDto> warnings = validationService.getProfileWarnings(1L, adminUser);

        assertThat(warnings).isEmpty();
    }

    @Test
    void warns_whenBirthDateIsAfterDeathDate() {
        ZonedDateTime dob = ZonedDateTime.now().minusYears(10);
        ZonedDateTime dod = ZonedDateTime.now().minusYears(40); // dod before dob
        Profile profile = buildProfile(1L, dob, dod, Gender.MALE);

        when(profileService.getProfileById(1L)).thenReturn(profile);
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of());
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of());

        List<ValidationWarningDto> warnings = validationService.getProfileWarnings(1L, adminUser);

        assertThat(warnings).hasSize(1);
        assertThat(warnings.get(0).getCode()).isEqualTo("BIRTH_AFTER_DEATH");
    }

    @Test
    void warns_whenMarriageDateBeforeHusbandBirth() {
        ZonedDateTime husbandDob = ZonedDateTime.now().minusYears(30);
        ZonedDateTime wifeDob = ZonedDateTime.now().minusYears(28);
        ZonedDateTime marriageDate = ZonedDateTime.now().minusYears(35); // before husband birth

        Profile husband = buildProfile(1L, husbandDob, null, Gender.MALE);
        Profile wife = buildProfile(2L, wifeDob, null, Gender.FEMALE);

        Marriage marriage = new Marriage();
        marriage.setId(1L);
        marriage.setHusband(husband);
        marriage.setWife(wife);
        marriage.setMarriageDate(marriageDate);

        husband.getMarriagesAsHusband().add(marriage);

        when(profileService.getProfileById(1L)).thenReturn(husband);
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of());
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of());

        List<ValidationWarningDto> warnings = validationService.getProfileWarnings(1L, adminUser);

        assertThat(warnings).isNotEmpty();
        assertThat(warnings.stream().anyMatch(w -> "MARRIAGE_BEFORE_BIRTH".equals(w.getCode()))).isTrue();
    }

    @Test
    void warns_whenMarriageDateAfterHusbandDeath() {
        ZonedDateTime husbandDob = ZonedDateTime.now().minusYears(80);
        ZonedDateTime husbandDod = ZonedDateTime.now().minusYears(20);
        ZonedDateTime wifeDob = ZonedDateTime.now().minusYears(75);
        ZonedDateTime marriageDate = ZonedDateTime.now().minusYears(10); // after husband death

        Profile husband = buildProfile(1L, husbandDob, husbandDod, Gender.MALE);
        Profile wife = buildProfile(2L, wifeDob, null, Gender.FEMALE);

        Marriage marriage = new Marriage();
        marriage.setId(1L);
        marriage.setHusband(husband);
        marriage.setWife(wife);
        marriage.setMarriageDate(marriageDate);

        husband.getMarriagesAsHusband().add(marriage);

        when(profileService.getProfileById(1L)).thenReturn(husband);
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of());
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of());

        List<ValidationWarningDto> warnings = validationService.getProfileWarnings(1L, adminUser);

        assertThat(warnings).isNotEmpty();
        assertThat(warnings.stream().anyMatch(w -> "MARRIAGE_AFTER_DEATH".equals(w.getCode()))).isTrue();
    }

    @Test
    void warns_whenChildBornBeforeParent() {
        ZonedDateTime parentDob = ZonedDateTime.now().minusYears(30);
        ZonedDateTime childDob = ZonedDateTime.now().minusYears(40); // child born before parent

        Profile parent = buildProfile(1L, parentDob, null, Gender.MALE);
        Profile child = buildProfile(2L, childDob, null, Gender.MALE);
        child.setFirstName("Child");
        child.setLastName("One");

        when(profileService.getProfileById(1L)).thenReturn(parent);
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of(child));
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of());

        List<ValidationWarningDto> warnings = validationService.getProfileWarnings(1L, adminUser);

        assertThat(warnings).isNotEmpty();
        assertThat(warnings.stream().anyMatch(w -> "CHILD_BORN_BEFORE_PARENT".equals(w.getCode()))).isTrue();
    }

    @Test
    void noWarnings_whenNoDatesSet() {
        Profile profile = buildProfile(1L, null, null, Gender.MALE);

        when(profileService.getProfileById(1L)).thenReturn(profile);
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of());
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of());

        List<ValidationWarningDto> warnings = validationService.getProfileWarnings(1L, adminUser);

        assertThat(warnings).isEmpty();
    }

    @Test
    void noWarning_whenMarriageDateIsNull() {
        ZonedDateTime dob = ZonedDateTime.now().minusYears(40);
        Profile husband = buildProfile(1L, dob, null, Gender.MALE);
        Profile wife = buildProfile(2L, dob, null, Gender.FEMALE);

        Marriage marriage = new Marriage();
        marriage.setId(1L);
        marriage.setHusband(husband);
        marriage.setWife(wife);
        marriage.setMarriageDate(null); // no date, no warning expected

        husband.getMarriagesAsHusband().add(marriage);

        when(profileService.getProfileById(1L)).thenReturn(husband);
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of());
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of());

        List<ValidationWarningDto> warnings = validationService.getProfileWarnings(1L, adminUser);

        assertThat(warnings).isEmpty();
    }
}
