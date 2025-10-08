package com.example.genealogie.controller;

import com.example.genealogie.dto.ProfileRequestDto;
import com.example.genealogie.dto.ProfileResponseDto;
import com.example.genealogie.mapper.ProfileMapper;
import com.example.genealogie.model.Profile;
import com.example.genealogie.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileMapper profileMapper;
    private final ProfileService profileService;

    @PostMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> createProfile(@RequestBody ProfileRequestDto requestDto,
                                                            @PathVariable Long userId) {
        Profile profile = profileMapper.toEntity(requestDto, userId);
        profileService.create(profile);

        return ResponseEntity.ok().body(profileMapper.toDto(profile));
    }
}
