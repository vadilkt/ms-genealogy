package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {

    private Long id;
    private String city;
    private String country;
    @Nullable
    private String region;
}
