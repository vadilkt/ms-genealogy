package com.example.genealogie.service;

import com.example.genealogie.dto.UserRequestDto;
import com.example.genealogie.model.User;

public interface UserService {
    User getUserById(Long userId);
    User createUser(UserRequestDto userRequestDto);
}
