package com.cinema.seatservice.service;

import com.cinema.seatservice.model.Seat;
import com.cinema.seatservice.model.SeatLock;
import com.cinema.seatservice.model.SeatStatus;
import com.cinema.seatservice.repository.SeatLockRepository;
import com.cinema.seatservice.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeatLockRepository seatLockRepository;

    @Transactional
    public List<Seat> initSeats(Long showtimeId, int count) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            seats.add(Seat.builder()
                    .showtimeId(showtimeId)
                    .seatNumber(i)
                    .status(SeatStatus.AVAILABLE)
                    .build());
        }
        return seatRepository.saveAll(seats);
    }

    public List<Seat> getSeats(Long showtimeId) {
        return seatRepository.findByShowtimeIdOrderBySeatNumberAsc(showtimeId);
    }


    @Transactional
    public void lockSeat(Long showtimeId, Integer seatNumber, Long userId) {
        Seat seat = seatRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat is not available");
        }

        seat.setStatus(SeatStatus.LOCKED);
        seatRepository.save(seat);

        SeatLock lock = SeatLock.builder()
                .showtimeId(showtimeId)
                .seatNumber(seatNumber)
                .lockedByUserId(userId)
                .lockedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        seatLockRepository.save(lock);
    }

    @Transactional
    public void confirmSeat(Long showtimeId, Integer seatNumber) {
        Seat seat = seatRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        
        // Remove lock if exists
        seatLockRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber)
                .ifPresent(seatLockRepository::delete);

        seat.setStatus(SeatStatus.SOLD);
        seatRepository.save(seat);
    }

    @Transactional
    public void releaseSeat(Long showtimeId, Integer seatNumber) {
        Seat seat = seatRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        // Remove lock if exists
        seatLockRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber)
                .ifPresent(seatLockRepository::delete);

        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);
    }

    @Scheduled(fixedRate = 60000) // Every minute
    @Transactional
    public void cleanupExpiredLocks() {
        List<SeatLock> expiredLocks = seatLockRepository.findByExpiresAtBefore(LocalDateTime.now());
        for (SeatLock lock : expiredLocks) {
            log.info("Releasing expired lock for showtime {} seat {}", lock.getShowtimeId(), lock.getSeatNumber());
            seatRepository.findByShowtimeIdAndSeatNumber(lock.getShowtimeId(), lock.getSeatNumber())
                    .ifPresent(seat -> {
                        if (seat.getStatus() == SeatStatus.LOCKED) {
                            seat.setStatus(SeatStatus.AVAILABLE);
                            seatRepository.save(seat);
                        }
                    });
            seatLockRepository.delete(lock);
        }
    }
}
