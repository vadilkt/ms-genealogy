package com.example.genealogie.service.impl;

import com.example.genealogie.dto.UserRequestDto;
import com.example.genealogie.mapper.UserMapper;
import com.example.genealogie.model.User;
import com.example.genealogie.repository.UserRepository;
import com.example.genealogie.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("User not found"));
    }

    @Override
    public User createUser(UserRequestDto userRequestDto) {
        if(userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already exists");
        }
        if(userRequestDto.getEmail() != null && userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new EntityExistsException("Email already exists");
        }
        User user = userMapper.toUser(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return userRepository.save(user);
    }
}
