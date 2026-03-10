package com.example.genealogie.controller;

import com.example.genealogie.dto.ChangePasswordRequestDto;
import com.example.genealogie.dto.UserDto;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(@AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        List<UserDto> users = userService.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
            @RequestBody @Valid ChangePasswordRequestDto requestDto,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        userService.changePassword(id, requestDto.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
