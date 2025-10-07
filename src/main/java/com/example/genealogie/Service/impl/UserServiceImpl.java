package com.example.genealogie.Service.impl;

import com.example.genealogie.Dto.UserRequestDto;
import com.example.genealogie.Mapper.UserMapper;
import com.example.genealogie.Model.User;
import com.example.genealogie.Repository.UserRepository;
import com.example.genealogie.Service.UserService;
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
        User user = userMapper.toUser(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return userRepository.save(user);
    }
}
