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
public class ProfessionalProfileRequestDto {
    @NotNull
    private String profession;
    @NotNull
    private String entreprise;
    @NotNull
    private String ville;
    @NotNull
    private ZonedDateTime dateDebut;
    @Nullable
    private ZonedDateTime dateFin;
    @Nullable
    private String description;

}
