package com.example.genealogie.dto;

import jakarta.annotation.Nullable;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcademicProfileRequestDto {
    @NonNull
    private String institution;
    @NonNull
    private String degree;
    @NonNull
    private String fieldOfStudy;
    @NonNull
    private ZonedDateTime startDate;
    @Nullable
    private ZonedDateTime endDate;
    @Nullable
    private String grade;
}
