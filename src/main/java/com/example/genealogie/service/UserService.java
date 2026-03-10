package com.example.genealogie.service;

import com.example.genealogie.dto.UserRequestDto;
import com.example.genealogie.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User getUserById(Long userId);

    User getUserByUsername(String username);

    User createUser(UserRequestDto userRequestDto);

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    void changePassword(Long userId, String newPassword);
}
