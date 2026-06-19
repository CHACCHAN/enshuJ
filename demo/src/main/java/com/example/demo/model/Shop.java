package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Shop {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String imagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
