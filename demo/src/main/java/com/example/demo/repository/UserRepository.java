package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcClient jdbcClient;

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setNickname(rs.getString("nickname"));
        return user;
    };

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<User> findByUsername(String username) {
        return jdbcClient.sql("SELECT * FROM users WHERE username = :username")
                .param("username", username)
                .query(USER_MAPPER)
                .optional();
    }

    public Optional<User> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM users WHERE id = :id")
                .param("id", id)
                .query(USER_MAPPER)
                .optional();
    }

    public User save(User user) {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcClient.sql("INSERT INTO users (username, password, nickname) VALUES (:username, :password, :nickname)")
                    .param("username", user.getUsername())
                    .param("password", user.getPassword())
                    .param("nickname", user.getNickname())
                    .update(keyHolder);
            user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            jdbcClient.sql("UPDATE users SET username = :username, password = :password, nickname = :nickname WHERE id = :id")
                    .param("username", user.getUsername())
                    .param("password", user.getPassword())
                    .param("nickname", user.getNickname())
                    .param("id", user.getId())
                    .update();
        }
        return user;
    }
}
