package com.cinema.seatservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_locks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long showtimeId;

    @Column(nullable = false)
    private Integer seatNumber;

    @Column(nullable = false)
    private LocalDateTime lockedAt;

    @Column(nullable = false)
    private Long lockedByUserId; // Assuming userId is Long

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
