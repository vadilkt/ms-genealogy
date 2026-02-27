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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(UserRequestDto userRequestDto) {
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new EntityExistsException("Ce nom d'utilisateur existe déjà");
        }
        if (userRequestDto.getEmail() != null && userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new EntityExistsException("Cet email existe déjà");
        }
        User user = userMapper.toUser(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return userRepository.save(user);
    }
}
