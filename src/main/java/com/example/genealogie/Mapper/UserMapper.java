package com.example.genealogie.Mapper;

import com.example.genealogie.Dto.UserRequestDto;
import com.example.genealogie.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toUser(UserRequestDto userRequestDto);
}
