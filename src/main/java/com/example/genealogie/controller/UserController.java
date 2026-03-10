package com.example.genealogie.controller;

import com.example.genealogie.dto.ChangePasswordRequestDto;
import com.example.genealogie.dto.UserDto;
import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        Page<UserDto> users = userService.findAll(pageable)
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name()));
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
