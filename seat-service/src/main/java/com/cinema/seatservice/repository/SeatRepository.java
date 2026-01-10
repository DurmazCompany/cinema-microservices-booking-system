package com.cinema.seatservice.repository;

import com.cinema.seatservice.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowtimeIdOrderBySeatNumberAsc(Long showtimeId);
    Optional<Seat> findByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);
}
