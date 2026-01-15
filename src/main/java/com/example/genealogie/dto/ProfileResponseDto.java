package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String gender;
    private ZonedDateTime dateOfBirth;
    private ZonedDateTime dateOfDeath;
    private Integer age;
    private String residence;
    private PlaceDto birthPlace;
    private PlaceDto deathPlace;
    private List<ProfessionalProfileResponseDto> professionalRecords;
    private List<AcademicProfileResponseDto> academicRecords;
}
