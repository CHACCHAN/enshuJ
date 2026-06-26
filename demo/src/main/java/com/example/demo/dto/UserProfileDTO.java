package com.example.demo.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long userId;
    private String username;
    private String displayName;
    private String bio;
    private String avatarPath;
}
