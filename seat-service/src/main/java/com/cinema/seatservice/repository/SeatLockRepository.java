package com.cinema.seatservice.repository;

import com.cinema.seatservice.model.SeatLock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {
    Optional<SeatLock> findByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);
    List<SeatLock> findByExpiresAtBefore(LocalDateTime now);
}
