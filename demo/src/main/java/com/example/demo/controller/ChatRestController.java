package com.example.demo.controller;

import com.example.demo.dto.ChatRoomDTO;
import com.example.demo.dto.CreateRoomRequest;
import com.example.demo.dto.MessageDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatService;
import com.example.demo.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatRestController {

    private final ChatService chatService;
    private final ProfileService profileService;
    private final UserRepository userRepository;

    public ChatRestController(ChatService chatService,
                              ProfileService profileService,
                              UserRepository userRepository) {
        this.chatService = chatService;
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @GetMapping("/chat/rooms")
    public List<ChatRoomDTO> getRooms(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return chatService.getRoomsForUser(user.getId());
    }

    @PostMapping("/chat/rooms")
    public ChatRoomDTO createRoom(@RequestBody CreateRoomRequest req,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return chatService.createRoom(req, user.getId());
    }

    @GetMapping("/chat/rooms/{roomId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long roomId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        try {
            return ResponseEntity.ok(chatService.getMessageHistory(roomId, user.getId(), page));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/users/search")
    public List<UserProfileDTO> searchUsers(@RequestParam String q,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return profileService.searchUsers(q, user.getId());
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }
}
