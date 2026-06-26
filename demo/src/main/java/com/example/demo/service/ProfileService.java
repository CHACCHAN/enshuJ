package com.example.demo.service;

import com.example.demo.dto.UpdateAccountRequest;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.model.User;
import com.example.demo.model.UserProfile;
import com.example.demo.repository.UserProfileRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(UserProfileRepository userProfileRepository,
                          UserRepository userRepository,
                          ImageService imageService,
                          PasswordEncoder passwordEncoder) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileDTO getOrCreateProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        UserProfile profile = userProfileRepository.findByUserId(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            p.setDisplayName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            userProfileRepository.upsert(p);
            return p;
        });
        return toDTO(user, profile);
    }

    public UserProfileDTO updateProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId).orElseThrow();
        UserProfile profile = userProfileRepository.findByUserId(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            return p;
        });
        if (req.getDisplayName() != null) profile.setDisplayName(req.getDisplayName());
        if (req.getBio() != null) profile.setBio(req.getBio());
        userProfileRepository.upsert(profile);
        return toDTO(user, profile);
    }

    public String updateAvatar(Long userId, MultipartFile file) throws IOException {
        String filename = imageService.saveAvatar(file);
        UserProfile profile = userProfileRepository.findByUserId(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            return p;
        });
        if (profile.getAvatarPath() != null) {
            imageService.deleteAvatar(profile.getAvatarPath());
        }
        profile.setAvatarPath(filename);
        userProfileRepository.upsert(profile);
        return filename;
    }

    public void updateAccount(Long userId, UpdateAccountRequest req) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("現在のパスワードが正しくありません");
        }
        if (req.getNewUsername() != null && !req.getNewUsername().isBlank()) {
            if (!req.getNewUsername().equals(user.getUsername()) &&
                    userRepository.findByUsername(req.getNewUsername()).isPresent()) {
                throw new IllegalArgumentException("このユーザー名は既に使用されています");
            }
            user.setUsername(req.getNewUsername());
        }
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }
        userRepository.save(user);
    }

    public List<UserProfileDTO> searchUsers(String query, Long excludeUserId) {
        return userRepository.searchByUsername(query).stream()
                .filter(u -> !u.getId().equals(excludeUserId))
                .map(u -> {
                    UserProfile p = userProfileRepository.findByUserId(u.getId()).orElse(null);
                    UserProfileDTO dto = new UserProfileDTO();
                    dto.setUserId(u.getId());
                    dto.setUsername(u.getUsername());
                    if (p != null) {
                        dto.setDisplayName(p.getDisplayName());
                        dto.setBio(p.getBio());
                        dto.setAvatarPath(p.getAvatarPath());
                    } else {
                        dto.setDisplayName(u.getNickname() != null ? u.getNickname() : u.getUsername());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private UserProfileDTO toDTO(User user, UserProfile profile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(profile.getDisplayName() != null
                ? profile.getDisplayName()
                : (user.getNickname() != null ? user.getNickname() : user.getUsername()));
        dto.setBio(profile.getBio());
        dto.setAvatarPath(profile.getAvatarPath());
        return dto;
    }
}
