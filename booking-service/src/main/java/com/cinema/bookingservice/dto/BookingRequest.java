package com.cinema.bookingservice.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long showtimeId;
    private Integer seatNumber;
}
