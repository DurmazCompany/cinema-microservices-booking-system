package com.cinema.seatservice.dto;

import lombok.Data;

@Data
public class LockRequest {
    private Integer seatNumber;
    private Long userId;
}
