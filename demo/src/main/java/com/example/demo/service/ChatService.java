package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.ChatRoom;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.model.UserProfile;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserProfileRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatRoomRepository chatRoomRepository,
                       MessageRepository messageRepository,
                       UserProfileRepository userProfileRepository,
                       UserRepository userRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<ChatRoomDTO> getRoomsForUser(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findByUserId(userId);
        return rooms.stream()
                .map(room -> enrichRoom(room, userId))
                .collect(Collectors.toList());
    }

    public ChatRoomDTO createRoom(CreateRoomRequest req, Long creatorId) {
        if ("DM".equals(req.getType())) {
            Long targetId = req.getMemberIds().get(0);
            return chatRoomRepository.findDmRoomBetween(creatorId, targetId)
                    .map(existing -> enrichRoom(existing, creatorId))
                    .orElseGet(() -> {
                        ChatRoom room = new ChatRoom();
                        room.setType("DM");
                        chatRoomRepository.save(room);
                        chatRoomRepository.addMember(room.getId(), creatorId);
                        chatRoomRepository.addMember(room.getId(), targetId);
                        return enrichRoom(room, creatorId);
                    });
        } else {
            ChatRoom room = new ChatRoom();
            room.setType("GROUP");
            room.setName(req.getName());
            chatRoomRepository.save(room);
            chatRoomRepository.addMember(room.getId(), creatorId);
            for (Long memberId : req.getMemberIds()) {
                if (!memberId.equals(creatorId)) {
                    chatRoomRepository.addMember(room.getId(), memberId);
                }
            }
            return enrichRoom(room, creatorId);
        }
    }

    public List<MessageDTO> getMessageHistory(Long roomId, Long userId, int page) {
        if (!chatRoomRepository.isMember(roomId, userId)) {
            throw new SecurityException("Not a member of this room");
        }
        List<Message> messages = messageRepository.findByRoomId(roomId, page);
        List<Long> senderIds = messages.stream()
                .filter(m -> m.getSenderId() != null)
                .map(Message::getSenderId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, UserProfile> profileMap = buildProfileMap(senderIds);
        return messages.stream()
                .map(m -> toMessageDTO(m, profileMap))
                .collect(Collectors.toList());
    }

    public MessageDTO sendMessage(Long roomId, Long senderId, String content) {
        if (!chatRoomRepository.isMember(roomId, senderId)) {
            throw new SecurityException("Not a member of this room");
        }
        Message message = new Message();
        message.setRoomId(roomId);
        message.setSenderId(senderId);
        message.setContent(content);
        messageRepository.save(message);

        UserProfile profile = userProfileRepository.findByUserId(senderId).orElse(null);
        Map<Long, UserProfile> profileMap = profile != null
                ? Map.of(senderId, profile) : Map.of();
        MessageDTO dto = toMessageDTO(message, profileMap);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, dto);

        // DM・GROUP 問わず、送信者以外の全メンバーに個人通知を送る
        // → 他のルームを見ていたり /chat を開いていないメンバーがサイドバーで受け取れる
        List<Long> memberIds = chatRoomRepository.findMemberUserIds(roomId);
        for (Long memberId : memberIds) {
            if (!memberId.equals(senderId)) {
                userRepository.findById(memberId).ifPresent(u ->
                        messagingTemplate.convertAndSendToUser(u.getUsername(), "/queue/messages", dto));
            }
        }
        return dto;
    }

    private ChatRoomDTO enrichRoom(ChatRoom room, Long viewingUserId) {
        List<Long> memberIds = chatRoomRepository.findMemberUserIds(room.getId());
        Map<Long, UserProfile> profileMap = buildProfileMap(memberIds);

        List<UserProfileDTO> members = memberIds.stream()
                .map(id -> {
                    UserProfile p = profileMap.get(id);
                    User u = userRepository.findById(id).orElse(null);
                    return toUserProfileDTO(id, u, p);
                })
                .collect(Collectors.toList());

        MessageDTO lastMessage = messageRepository.findLastByRoomId(room.getId())
                .map(m -> toMessageDTO(m, profileMap))
                .orElse(null);

        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setType(room.getType());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setMembers(members);
        dto.setLastMessage(lastMessage);
        return dto;
    }

    private MessageDTO toMessageDTO(Message m, Map<Long, UserProfile> profileMap) {
        MessageDTO dto = new MessageDTO();
        dto.setId(m.getId());
        dto.setRoomId(m.getRoomId());
        dto.setSenderId(m.getSenderId());
        dto.setSentAt(m.getSentAt());
        dto.setContent(m.getContent());
        if (m.getSenderId() != null) {
            UserProfile p = profileMap.get(m.getSenderId());
            dto.setSenderDisplayName(p != null && p.getDisplayName() != null
                    ? p.getDisplayName()
                    : userRepository.findById(m.getSenderId()).map(User::getNickname).orElse("Unknown"));
            dto.setSenderAvatarPath(p != null ? p.getAvatarPath() : null);
        }
        return dto;
    }

    private Map<Long, UserProfile> buildProfileMap(List<Long> userIds) {
        if (userIds.isEmpty()) return Map.of();
        return userProfileRepository.findByUserIds(userIds).stream()
                .collect(Collectors.toMap(UserProfile::getUserId, Function.identity()));
    }

    private UserProfileDTO toUserProfileDTO(Long userId, User user, UserProfile profile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(userId);
        if (user != null) dto.setUsername(user.getUsername());
        if (profile != null) {
            dto.setDisplayName(profile.getDisplayName());
            dto.setBio(profile.getBio());
            dto.setAvatarPath(profile.getAvatarPath());
        }
        return dto;
    }
}
