package com.example.demo.repository;

import com.example.demo.model.Shop;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ShopRepository {

    private final JdbcClient jdbcClient;

    private static final RowMapper<Shop> SHOP_MAPPER = (rs, rowNum) -> {
        Shop shop = new Shop();
        shop.setId(rs.getLong("id"));
        shop.setName(rs.getString("name"));
        shop.setPrice(rs.getBigDecimal("price"));
        shop.setStock(rs.getInt("stock"));
        shop.setImagePath(rs.getString("image_path"));
        shop.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        shop.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return shop;
    };

    public ShopRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Shop> findAll() {
        return jdbcClient.sql("SELECT * FROM shops ORDER BY id")
                .query(SHOP_MAPPER)
                .list();
    }

    public Optional<Shop> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM shops WHERE id = :id")
                .param("id", id)
                .query(SHOP_MAPPER)
                .optional();
    }

    public Optional<Shop> findByName(String name) {
        return jdbcClient.sql("SELECT * FROM shops WHERE name = :name")
                .param("name", name)
                .query(SHOP_MAPPER)
                .optional();
    }

    public boolean existsByName(String name) {
        Integer count = jdbcClient.sql("SELECT COUNT(*) FROM shops WHERE name = :name")
                .param("name", name)
                .query(Integer.class)
                .single();
        return count != null && count > 0;
    }

    public List<Shop> findByStockGreaterThan(int stock) {
        return jdbcClient.sql("SELECT * FROM shops WHERE stock > :stock")
                .param("stock", stock)
                .query(SHOP_MAPPER)
                .list();
    }

    public List<Shop> findByPriceBetween(BigDecimal min, BigDecimal max) {
        return jdbcClient.sql("SELECT * FROM shops WHERE price BETWEEN :min AND :max")
                .param("min", min)
                .param("max", max)
                .query(SHOP_MAPPER)
                .list();
    }

    public Shop save(Shop shop) {
        LocalDateTime now = LocalDateTime.now();
        if (shop.getId() == null) {
            shop.setCreatedAt(now);
            shop.setUpdatedAt(now);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcClient.sql("INSERT INTO shops (name, price, stock, image_path, created_at, updated_at) VALUES (:name, :price, :stock, :imagePath, :createdAt, :updatedAt)")
                    .param("name", shop.getName())
                    .param("price", shop.getPrice())
                    .param("stock", shop.getStock())
                    .param("imagePath", shop.getImagePath())
                    .param("createdAt", shop.getCreatedAt())
                    .param("updatedAt", shop.getUpdatedAt())
                    .update(keyHolder, "id");
            shop.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            shop.setUpdatedAt(now);
            jdbcClient.sql("UPDATE shops SET name = :name, price = :price, stock = :stock, image_path = :imagePath, updated_at = :updatedAt WHERE id = :id")
                    .param("name", shop.getName())
                    .param("price", shop.getPrice())
                    .param("stock", shop.getStock())
                    .param("imagePath", shop.getImagePath())
                    .param("updatedAt", shop.getUpdatedAt())
                    .param("id", shop.getId())
                    .update();
        }
        return shop;
    }

    public void deleteById(Long id) {
        jdbcClient.sql("DELETE FROM shops WHERE id = :id")
                .param("id", id)
                .update();
    }
}
