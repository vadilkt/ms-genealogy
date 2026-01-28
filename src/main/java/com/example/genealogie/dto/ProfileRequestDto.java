package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequestDto {
    @NotNull
    private String firstName;
    private String lastName;
    @NotNull
    private String gender;
    @NotNull
    private ZonedDateTime dateOfBirth;
    @Nullable
    private ZonedDateTime dateOfDeath;
    @NotNull
    private String residence;
    @Nullable
    private Long birthPlaceId;
    @Nullable
    private Long deathPlaceId;
}
