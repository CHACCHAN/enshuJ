package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderDisplayName;
    private String senderAvatarPath;
    private String content;
    private LocalDateTime sentAt;
}
