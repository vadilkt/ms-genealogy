package com.example.genealogie.mapper;

import com.example.genealogie.dto.ProfileRequestDto;
import com.example.genealogie.dto.ProfileResponseDto;
import com.example.genealogie.model.Gender;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.service.UserService;
import org.mapstruct.Mapper;
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


    public Profile toEntity(ProfileRequestDto requestDto, Long userId) {
        if (requestDto == null) {
            return null;
        }

        Profile profile = new Profile();

        profile.setFirstName(requestDto.getFirstName());
        profile.setLastName(requestDto.getLastName());
        profile.setResidence(requestDto.getResidence());
        profile.setDateOfBirth(requestDto.getDateOfBirth());
        profile.setDateOfDeath(requestDto.getDateOfDeath());

        profile.setGender(getGender(requestDto.getGender()));

        if (userId != null) {
            User user = getUserById(userId);
            profile.setUser(user);
        }

        return profile;
    }

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
