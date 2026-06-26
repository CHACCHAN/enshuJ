package com.example.demo.repository;

import com.example.demo.model.Message;
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
public class MessageRepository {

    private final JdbcClient jdbcClient;

    private static final RowMapper<Message> MESSAGE_MAPPER = (rs, rowNum) -> {
        Message m = new Message();
        m.setId(rs.getLong("id"));
        m.setRoomId(rs.getLong("room_id"));
        long senderId = rs.getLong("sender_id");
        m.setSenderId(rs.wasNull() ? null : senderId);
        m.setContent(rs.getString("content"));
        m.setSentAt(rs.getObject("sent_at", LocalDateTime.class));
        return m;
    };

    public MessageRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Message> findByRoomId(Long roomId, int page) {
        int limit = 50;
        int offset = page * limit;
        return jdbcClient.sql("""
                SELECT * FROM (
                    SELECT * FROM messages WHERE room_id = :roomId
                    ORDER BY sent_at DESC
                    LIMIT :limit OFFSET :offset
                ) sub ORDER BY sent_at ASC
                """)
                .param("roomId", roomId)
                .param("limit", limit)
                .param("offset", offset)
                .query(MESSAGE_MAPPER)
                .list();
    }

    public Optional<Message> findLastByRoomId(Long roomId) {
        return jdbcClient.sql(
                "SELECT * FROM messages WHERE room_id = :roomId ORDER BY sent_at DESC LIMIT 1")
                .param("roomId", roomId)
                .query(MESSAGE_MAPPER)
                .optional();
    }

    public Message save(Message message) {
        message.setSentAt(LocalDateTime.now());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                INSERT INTO messages (room_id, sender_id, content, sent_at)
                VALUES (:roomId, :senderId, :content, :sentAt)
                """)
                .param("roomId", message.getRoomId())
                .param("senderId", message.getSenderId())
                .param("content", message.getContent())
                .param("sentAt", message.getSentAt())
                .update(keyHolder, "id");
        message.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return message;
    }
}
