package com.example.genealogie.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequestDto {

    @NotBlank(message = "La ville est requise")
    private String city;

    @NotBlank(message = "Le pays est requis")
    private String country;

    @Nullable
    private String region;
}
