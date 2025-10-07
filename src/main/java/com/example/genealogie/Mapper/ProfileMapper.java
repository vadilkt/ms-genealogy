package com.example.genealogie.Mapper;

import com.example.genealogie.Dto.ProfileRequestDto;
import com.example.genealogie.Dto.ProfileResponseDto;
import com.example.genealogie.Model.Gender;
import com.example.genealogie.Model.Profile;
import com.example.genealogie.Model.User;
import com.example.genealogie.Service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProfileMapper {

    @Autowired
    private UserService userService;

    public ProfileResponseDto toDto(Profile profile) {
        ProfileResponseDto dto = new ProfileResponseDto();

        dto.setId(profile.getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setGender(profile.getGender().name());
        dto.setResidence(profile.getResidence());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setDateOfDeath(profile.getDateOfDeath());

        return dto;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", expression = "java(getUserById(userId))")
    @Mapping(target = "gender", expression = "java(stringToGender(requestDto.getGender()))")
    public abstract Profile toEntity(ProfileRequestDto requestDto, Long userId);

    @Named("stringToGender")
    private Gender getGender(String gender) {
        if(gender == null) {
            return null;
        }

        return switch (gender.toLowerCase()) {
            case "male" -> Gender.MALE;
            case "female" -> Gender.FEMALE;
            default -> null;
        };
    }

    @Named("getUserById")
    private User getUserById(Long id) {
        return userService.getUserById(id);
    }
}
