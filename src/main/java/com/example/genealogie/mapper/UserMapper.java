package com.example.genealogie.mapper;

import com.example.genealogie.dto.UserRequestDto;
import com.example.genealogie.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toUser(UserRequestDto userRequestDto);
}
