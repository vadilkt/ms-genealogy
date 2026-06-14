package com.example.genealogie.service;

import com.example.genealogie.model.Gender;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.repository.MarriageRepository;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.impl.GedcomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GedcomServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private MarriageRepository marriageRepository;

    @InjectMocks
    private GedcomServiceImpl gedcomService;

    private User admin;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("pass");
        admin.setRole(UserRole.ADMIN);
    }

    private Profile profile(Long id, String first, String last, Gender gender) {
        Profile p = new Profile();
        p.setId(id);
        p.setFirstName(first);
        p.setLastName(last);
        p.setGender(gender);
        return p;
    }

    @Test
    void export_buildsIndiAndFamRecords() {
        Profile dad = profile(1L, "John", "Doe", Gender.MALE);
        Profile mom = profile(2L, "Jane", "Doe", Gender.FEMALE);
        Profile kid = profile(3L, "Junior", "Doe", Gender.MALE);
        kid.setFather(dad);
        kid.setMother(mom);

        when(profileRepository.findAll()).thenReturn(List.of(dad, mom, kid));
        when(marriageRepository.findAll()).thenReturn(List.of());

        String g = gedcomService.exportGedcom();

        assertThat(g).contains("0 HEAD");
        assertThat(g).contains("2 VERS 5.5.1");
        assertThat(g).contains("0 @I3@ INDI");
        assertThat(g).contains("1 NAME Junior /Doe/");
        assertThat(g).contains("1 SEX M");
        assertThat(g).contains("1 FAMC @F1@");
        assertThat(g).contains("0 @F1@ FAM");
        assertThat(g).contains("1 HUSB @I1@");
        assertThat(g).contains("1 WIFE @I2@");
        assertThat(g).contains("1 CHIL @I3@");
        assertThat(g).endsWith("0 TRLR\n");
    }

    @Test
    void import_createsProfilesLinksAndMarriage() {
        String ged = String.join("\n",
                "0 HEAD",
                "0 @I1@ INDI",
                "1 NAME John /Doe/",
                "1 SEX M",
                "1 BIRT",
                "2 DATE ABT 1850",
                "0 @I2@ INDI",
                "1 NAME Jane /Roe/",
                "1 SEX F",
                "0 @I3@ INDI",
                "1 NAME Junior /Doe/",
                "1 SEX M",
                "0 @F1@ FAM",
                "1 HUSB @I1@",
                "1 WIFE @I2@",
                "1 CHIL @I3@",
                "1 MARR",
                "2 DATE 12 JUN 1870",
                "0 TRLR");

        when(profileRepository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        int created = gedcomService.importGedcom(ged, admin);

        assertThat(created).isEqualTo(3);
        // 3 créations + 1 mise à jour de l'enfant (père & mère) = 4 sauvegardes de profil
        verify(profileRepository, times(4)).save(any(Profile.class));
        // Le couple complet engendre un mariage
        verify(marriageRepository).save(any());
    }
}
