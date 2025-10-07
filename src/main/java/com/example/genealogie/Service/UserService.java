package com.example.genealogie.Service;

import com.example.genealogie.Dto.UserRequestDto;
import com.example.genealogie.Model.User;

public interface UserService {
    User getUserById(Long userId);
    User createUser(UserRequestDto userRequestDto);
}
