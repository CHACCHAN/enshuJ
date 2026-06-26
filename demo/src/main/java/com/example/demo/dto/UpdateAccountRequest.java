package com.example.demo.dto;

import lombok.Data;

@Data
public class UpdateAccountRequest {
    private String newUsername;
    private String currentPassword;
    private String newPassword;
}
