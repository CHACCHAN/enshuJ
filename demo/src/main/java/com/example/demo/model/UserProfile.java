package com.example.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfile {
    private Long userId;
    private String displayName;
    private String bio;
    private String avatarPath;
    private LocalDateTime updatedAt;
}
