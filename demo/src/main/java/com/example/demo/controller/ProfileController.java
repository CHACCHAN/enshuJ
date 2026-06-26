package com.example.demo.controller;

import com.example.demo.dto.UpdateAccountRequest;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ImageService;
import com.example.demo.service.ProfileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public ProfileController(ProfileService profileService,
                             UserRepository userRepository,
                             ImageService imageService) {
        this.profileService = profileService;
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    @GetMapping("/profile")
    public UserProfileDTO getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return profileService.getOrCreateProfile(user.getId());
    }

    @PutMapping("/profile")
    public UserProfileDTO updateProfile(@RequestBody UpdateProfileRequest req,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return profileService.updateProfile(user.getId(), req);
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = getUser(userDetails);
            String filename = profileService.updateAvatar(user.getId(), file);
            return ResponseEntity.ok(Map.of("avatarPath", filename));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/avatars/{filename:.+}")
    public ResponseEntity<Resource> serveAvatar(@PathVariable String filename) {
        try {
            Path file = imageService.resolveAvatar(filename);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account")
    public Map<String, String> getAccount(@AuthenticationPrincipal UserDetails userDetails) {
        return Map.of("username", userDetails.getUsername());
    }

    @PutMapping("/account")
    public ResponseEntity<Void> updateAccount(@RequestBody UpdateAccountRequest req,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        try {
            profileService.updateAccount(user.getId(), req);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }
}
