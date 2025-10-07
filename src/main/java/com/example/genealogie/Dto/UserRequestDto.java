package com.example.genealogie.Dto;

import com.example.genealogie.Model.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String username;
    private String password;
    private String email;
    private UserRole role;
}
