package com.example.demo.repository;

import com.example.demo.model.ChatRoom;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ChatRoomRepository {

    private final JdbcClient jdbcClient;

    private static final RowMapper<ChatRoom> ROOM_MAPPER = (rs, rowNum) -> {
        ChatRoom r = new ChatRoom();
        r.setId(rs.getLong("id"));
        r.setName(rs.getString("name"));
        r.setType(rs.getString("type"));
        r.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return r;
    };

    public ChatRoomRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<ChatRoom> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM chat_rooms WHERE id = :id")
                .param("id", id)
                .query(ROOM_MAPPER)
                .optional();
    }

    public List<ChatRoom> findByUserId(Long userId) {
        return jdbcClient.sql("""
                SELECT cr.* FROM chat_rooms cr
                JOIN chat_room_members crm ON cr.id = crm.room_id
                WHERE crm.user_id = :userId
                ORDER BY (SELECT MAX(sent_at) FROM messages WHERE room_id = cr.id) DESC NULLS LAST
                """)
                .param("userId", userId)
                .query(ROOM_MAPPER)
                .list();
    }

    public Optional<ChatRoom> findDmRoomBetween(Long userA, Long userB) {
        return jdbcClient.sql("""
                SELECT cr.* FROM chat_rooms cr
                WHERE cr.type = 'DM'
                AND EXISTS (SELECT 1 FROM chat_room_members WHERE room_id = cr.id AND user_id = :userA)
                AND EXISTS (SELECT 1 FROM chat_room_members WHERE room_id = cr.id AND user_id = :userB)
                LIMIT 1
                """)
                .param("userA", userA)
                .param("userB", userB)
                .query(ROOM_MAPPER)
                .optional();
    }

    public ChatRoom save(ChatRoom room) {
        room.setCreatedAt(LocalDateTime.now());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO chat_rooms (name, type, created_at) VALUES (:name, :type, :createdAt)")
                .param("name", room.getName())
                .param("type", room.getType())
                .param("createdAt", room.getCreatedAt())
                .update(keyHolder, "id");
        room.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return room;
    }

    public void addMember(Long roomId, Long userId) {
        jdbcClient.sql("INSERT INTO chat_room_members (room_id, user_id) VALUES (:roomId, :userId)")
                .param("roomId", roomId)
                .param("userId", userId)
                .update();
    }

    public List<Long> findMemberUserIds(Long roomId) {
        return jdbcClient.sql("SELECT user_id FROM chat_room_members WHERE room_id = :roomId")
                .param("roomId", roomId)
                .query(Long.class)
                .list();
    }

    public boolean isMember(Long roomId, Long userId) {
        Integer count = jdbcClient.sql(
                "SELECT COUNT(*) FROM chat_room_members WHERE room_id = :roomId AND user_id = :userId")
                .param("roomId", roomId)
                .param("userId", userId)
                .query(Integer.class)
                .single();
        return count != null && count > 0;
    }
}
