package com.example.genealogie.mapper;

import com.example.genealogie.dto.AcademicProfileRequestDto;
import com.example.genealogie.dto.AcademicProfileResponseDto;
import com.example.genealogie.model.AcademicProfile;
import com.example.genealogie.model.Profile;
import com.example.genealogie.service.ProfileService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AcademicProfileMapper {
    @Autowired
    protected ProfileService profileService;

    public abstract AcademicProfileResponseDto toDto(AcademicProfile academicProfile);


    @Mapping(source = "profileId", target = "profile", qualifiedByName = "getProfileById")
    @Mapping(target = "id", ignore = true)
    public abstract AcademicProfile toEntity(AcademicProfileRequestDto dto, Long profileId);

    @Mapping(target = "id", ignore = true)
    public abstract AcademicProfile update(@MappingTarget AcademicProfile target, AcademicProfile source);


    @Named("getProfileById")
    protected Profile getProfileById(Long profileId) {
        if (profileId == null) {
            return null;
        }
        return profileService.getProfileById(profileId);
    }
}
