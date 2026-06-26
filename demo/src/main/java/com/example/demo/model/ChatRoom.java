package com.example.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRoom {
    private Long id;
    private String name;
    private String type; // "DM" or "GROUP"
    private LocalDateTime createdAt;
}
