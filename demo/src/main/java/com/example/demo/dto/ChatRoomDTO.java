package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatRoomDTO {
    private Long id;
    private String name;
    private String type;
    private LocalDateTime createdAt;
    private List<UserProfileDTO> members;
    private MessageDTO lastMessage;
}
