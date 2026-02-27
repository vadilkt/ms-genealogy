package com.example.genealogie.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetParentRequestDto {

    @NotNull(message = "L'id du parent est requis")
    private Long parentProfileId;
}
