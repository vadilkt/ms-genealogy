package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private ZonedDateTime dateOfBirth;
    private ZonedDateTime dateOfDeath;
    private String residence;
}
