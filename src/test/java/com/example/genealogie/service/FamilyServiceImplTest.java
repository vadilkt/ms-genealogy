package com.example.genealogie.service;

import com.example.genealogie.dto.CreateMarriageRequestDto;
import com.example.genealogie.dto.MarriageResponseDto;
import com.example.genealogie.mapper.ProfileMapper;
import com.example.genealogie.model.Gender;
import com.example.genealogie.model.Marriage;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.repository.MarriageRepository;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.impl.FamilyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FamilyServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private MarriageRepository marriageRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private FamilyServiceImpl familyService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(99L);
        adminUser.setUsername("admin");
        adminUser.setPassword("pass");
        adminUser.setRole(UserRole.ADMIN);
    }

    private Profile buildProfile(Long id, Gender gender) {
        Profile p = new Profile();
        p.setId(id);
        p.setFirstName("First" + id);
        p.setLastName("Last" + id);
        p.setGender(gender);
        p.setMarriagesAsHusband(new ArrayList<>());
        return p;
    }

    // --- setFather tests ---

    @Test
    void setFather_throwsWhenProfileIsSelf() {
        assertThatThrownBy(() -> familyService.setFather(1L, 1L, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("propre père");
    }

    @Test
    void setFather_throwsWhenFatherIsNotMale() {
        Profile child = buildProfile(1L, Gender.MALE);
        Profile notFather = buildProfile(2L, Gender.FEMALE); // wrong gender

        when(profileService.getProfileById(1L)).thenReturn(child);
        when(profileService.isOwnerOrAdmin(child, adminUser)).thenReturn(true);
        when(profileService.getProfileById(2L)).thenReturn(notFather);

        assertThatThrownBy(() -> familyService.setFather(1L, 2L, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("masculin");
    }

    @Test
    void setFather_throwsWhenCycleDetected() {
        Profile child = buildProfile(1L, Gender.MALE);
        Profile father = buildProfile(2L, Gender.MALE);

        when(profileService.getProfileById(1L)).thenReturn(child);
        when(profileService.isOwnerOrAdmin(child, adminUser)).thenReturn(true);
        when(profileService.getProfileById(2L)).thenReturn(father);
        // Simulate candidate (2) is already a descendant of root (1)
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of(father));
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> familyService.setFather(1L, 2L, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cycle");
    }

    @Test
    void setFather_throwsWhenNotOwnerOrAdmin() {
        User regularUser = new User();
        regularUser.setId(5L);
        regularUser.setUsername("user");
        regularUser.setPassword("pass");
        regularUser.setRole(UserRole.USER);

        Profile child = buildProfile(1L, Gender.MALE);

        when(profileService.getProfileById(1L)).thenReturn(child);
        when(profileService.isOwnerOrAdmin(child, regularUser)).thenReturn(false);

        assertThatThrownBy(() -> familyService.setFather(1L, 2L, regularUser))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    // --- setMother tests ---

    @Test
    void setMother_throwsWhenProfileIsSelf() {
        assertThatThrownBy(() -> familyService.setMother(1L, 1L, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("propre mère");
    }

    @Test
    void setMother_throwsWhenMotherIsNotFemale() {
        Profile child = buildProfile(1L, Gender.FEMALE);
        Profile notMother = buildProfile(2L, Gender.MALE); // wrong gender

        when(profileService.getProfileById(1L)).thenReturn(child);
        when(profileService.isOwnerOrAdmin(child, adminUser)).thenReturn(true);
        when(profileService.getProfileById(2L)).thenReturn(notMother);

        assertThatThrownBy(() -> familyService.setMother(1L, 2L, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("féminin");
    }

    // --- addMarriage tests ---

    @Test
    void addMarriage_throwsWhenBothSameGenderMale() {
        Profile husband = buildProfile(1L, Gender.MALE);
        Profile anotherMale = buildProfile(2L, Gender.MALE);

        when(profileService.getProfileById(1L)).thenReturn(husband);
        when(profileService.isOwnerOrAdmin(husband, adminUser)).thenReturn(true);
        when(profileService.getProfileById(2L)).thenReturn(anotherMale);

        CreateMarriageRequestDto request = new CreateMarriageRequestDto(2L, null, null);

        assertThatThrownBy(() -> familyService.addMarriage(1L, request, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("homme et une femme");
    }

    @Test
    void addMarriage_throwsWhenBothSameGenderFemale() {
        Profile wife = buildProfile(1L, Gender.FEMALE);
        Profile anotherFemale = buildProfile(2L, Gender.FEMALE);

        when(profileService.getProfileById(1L)).thenReturn(wife);
        when(profileService.isOwnerOrAdmin(wife, adminUser)).thenReturn(true);
        when(profileService.getProfileById(2L)).thenReturn(anotherFemale);

        CreateMarriageRequestDto request = new CreateMarriageRequestDto(2L, null, null);

        assertThatThrownBy(() -> familyService.addMarriage(1L, request, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("homme et une femme");
    }

    @Test
    void addMarriage_throwsWhenWifeAlreadyMarried() {
        Profile husband = buildProfile(1L, Gender.MALE);
        Profile wife = buildProfile(2L, Gender.FEMALE);

        when(profileService.getProfileById(1L)).thenReturn(husband);
        when(profileService.isOwnerOrAdmin(husband, adminUser)).thenReturn(true);
        when(profileService.getProfileById(2L)).thenReturn(wife);
        when(marriageRepository.existsByWife_Id(2L)).thenReturn(true); // already married

        CreateMarriageRequestDto request = new CreateMarriageRequestDto(2L, null, null);

        assertThatThrownBy(() -> familyService.addMarriage(1L, request, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà mariée");
    }

    @Test
    void addMarriage_successWhenValidMaleAndFemale() {
        Profile husband = buildProfile(1L, Gender.MALE);
        Profile wife = buildProfile(2L, Gender.FEMALE);
        wife.setMarriageAsWife(null);

        when(profileService.getProfileById(1L)).thenReturn(husband);
        when(profileService.isOwnerOrAdmin(husband, adminUser)).thenReturn(true);
        when(profileService.getProfileById(2L)).thenReturn(wife);
        when(marriageRepository.existsByWife_Id(2L)).thenReturn(false);

        Marriage savedMarriage = new Marriage();
        savedMarriage.setId(10L);
        savedMarriage.setHusband(husband);
        savedMarriage.setWife(wife);
        when(marriageRepository.save(any(Marriage.class))).thenReturn(savedMarriage);

        when(profileMapper.toDtoSummary(husband)).thenReturn(null);
        when(profileMapper.toDtoSummary(wife)).thenReturn(null);

        CreateMarriageRequestDto request = new CreateMarriageRequestDto(2L, null, null);
        MarriageResponseDto result = familyService.addMarriage(1L, request, adminUser);

        org.assertj.core.api.Assertions.assertThat(result).isNotNull();
        org.assertj.core.api.Assertions.assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void setMother_throwsWhenCycleDetected() {
        Profile child = buildProfile(1L, Gender.MALE);
        Profile mother = buildProfile(2L, Gender.FEMALE);

        when(profileService.getProfileById(1L)).thenReturn(child);
        when(profileService.isOwnerOrAdmin(child, adminUser)).thenReturn(true);
        when(profileService.getProfileById(2L)).thenReturn(mother);
        // Simulate candidate (2) is already a descendant of root (1) via mother link
        when(profileRepository.findByFather_Id(1L)).thenReturn(List.of());
        when(profileRepository.findByMother_Id(1L)).thenReturn(List.of(mother));

        assertThatThrownBy(() -> familyService.setMother(1L, 2L, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cycle");
    }
}
