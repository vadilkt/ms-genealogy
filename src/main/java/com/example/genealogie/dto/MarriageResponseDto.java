package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarriageResponseDto {

    private Long id;
    private Long husbandId;
    private Long wifeId;
    private ProfileResponseDto husband;
    private ProfileResponseDto wife;
    private ZonedDateTime marriageDate;
    private ZonedDateTime endDate;
}
