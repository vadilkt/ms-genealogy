package com.example.genealogie.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMarriageRequestDto {

    /** Id du profil de l'époux/épouse (l'autre personne du mariage). */
    @NotNull(message = "L'id du conjoint est requis")
    private Long spouseProfileId;

    @Nullable
    private ZonedDateTime marriageDate;

    @Nullable
    private ZonedDateTime endDate;
}
