package com.example.genealogie.service;

import com.example.genealogie.dto.UserRequestDto;
import com.example.genealogie.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Long userId);

    User getUserByUsername(String username);

    User createUser(UserRequestDto userRequestDto);

    List<User> findAll();

    void changePassword(Long userId, String newPassword);
}
