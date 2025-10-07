package com.example.genealogie.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ProfileResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private ZonedDateTime dateOfBirth;
    private ZonedDateTime dateOfDeath;
    private String residence;
}
