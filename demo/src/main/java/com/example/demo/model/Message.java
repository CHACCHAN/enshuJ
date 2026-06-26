package com.example.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
}
