package com.example.demo.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long roomId;
    private String content;
}
