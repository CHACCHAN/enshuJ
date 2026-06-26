package com.example.demo.repository;

import com.example.demo.model.UserProfile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserProfileRepository {

    private final JdbcClient jdbcClient;

    private static final RowMapper<UserProfile> PROFILE_MAPPER = (rs, rowNum) -> {
        UserProfile p = new UserProfile();
        p.setUserId(rs.getLong("user_id"));
        p.setDisplayName(rs.getString("display_name"));
        p.setBio(rs.getString("bio"));
        p.setAvatarPath(rs.getString("avatar_path"));
        p.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return p;
    };

    public UserProfileRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<UserProfile> findByUserId(Long userId) {
        return jdbcClient.sql("SELECT * FROM user_profiles WHERE user_id = :userId")
                .param("userId", userId)
                .query(PROFILE_MAPPER)
                .optional();
    }

    public List<UserProfile> findByUserIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return jdbcClient.sql("SELECT * FROM user_profiles WHERE user_id IN (:ids)")
                .param("ids", ids)
                .query(PROFILE_MAPPER)
                .list();
    }

    public void upsert(UserProfile profile) {
        profile.setUpdatedAt(LocalDateTime.now());
        jdbcClient.sql("""
                MERGE INTO user_profiles (user_id, display_name, bio, avatar_path, updated_at)
                KEY (user_id)
                VALUES (:userId, :displayName, :bio, :avatarPath, :updatedAt)
                """)
                .param("userId", profile.getUserId())
                .param("displayName", profile.getDisplayName())
                .param("bio", profile.getBio())
                .param("avatarPath", profile.getAvatarPath())
                .param("updatedAt", profile.getUpdatedAt())
                .update();
    }
}
