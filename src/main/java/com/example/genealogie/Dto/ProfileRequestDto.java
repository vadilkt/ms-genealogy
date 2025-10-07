package com.example.genealogie.Dto;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ProfileRequestDto {
    @NotNull
    private String firstName;
    private String lastName;
    @NotNull
    private String gender;
    @NotNull
    private ZonedDateTime dateOfBirth;
    @NotNull
    private ZonedDateTime dateOfDeath;
    @NotNull
    private String residence;
}
