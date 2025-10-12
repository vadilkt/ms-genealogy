package com.example.genealogie.mapper;

import com.example.genealogie.dto.ProfessionalProfileRequestDto;
import com.example.genealogie.dto.ProfessionalProfileResponseDto;
import com.example.genealogie.model.ProfessionalProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.service.ProfileService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProfessionalProfileMapper {

    @Autowired
    protected ProfileService profileService;

    public abstract ProfessionalProfileResponseDto toDto(ProfessionalProfile professionalProfile);

    @Mapping(source = "profileId", target = "profile", qualifiedByName = "getProfileById")
    @Mapping(source = "dto.profession", target = "profession")
    @Mapping(source = "dto.entreprise", target = "entreprise")
    @Mapping(source = "dto.ville", target = "ville")
    @Mapping(source = "dto.dateDebut", target = "dateDebut")
    @Mapping(source = "dto.dateFin", target = "dateFin")
    @Mapping(source = "dto.description", target = "description")
    @Mapping(target = "id", ignore = true)
    public abstract ProfessionalProfile toEntity(ProfessionalProfileRequestDto dto, Long profileId);

    @Named("getProfileById")
    protected Profile getProfileById(Long profileId) {
        if (profileId == null) {
            return null;
        }
        return profileService.getProfileById(profileId);
    }
}