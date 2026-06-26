package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateRoomRequest {
    private String type;
    private String name;
    private List<Long> memberIds;
}
