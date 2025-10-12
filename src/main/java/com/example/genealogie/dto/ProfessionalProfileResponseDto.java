package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionalProfileResponseDto {
    private Long id;
    private String profession;
    private String entreprise;
    private String ville;
    private ZonedDateTime dateDebut;
    @Nullable
    private ZonedDateTime dateFin;
    private String description;
}
