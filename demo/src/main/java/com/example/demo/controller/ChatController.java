package com.example.demo.controller;

import com.example.demo.dto.SendMessageRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload SendMessageRequest req, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        chatService.sendMessage(req.getRoomId(), user.getId(), req.getContent());
    }
}
