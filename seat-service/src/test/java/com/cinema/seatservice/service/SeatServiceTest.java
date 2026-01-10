package com.cinema.seatservice.service;

import com.cinema.seatservice.model.Seat;
import com.cinema.seatservice.model.SeatLock;
import com.cinema.seatservice.model.SeatStatus;
import com.cinema.seatservice.repository.SeatLockRepository;
import com.cinema.seatservice.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatLockRepository seatLockRepository;

    @InjectMocks
    private SeatService seatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void lockSeat_Success() {
        Seat seat = new Seat(1L, 1L, 1, SeatStatus.AVAILABLE);
        when(seatRepository.findByShowtimeIdAndSeatNumber(1L, 1)).thenReturn(Optional.of(seat));

        seatService.lockSeat(1L, 1, 100L);

        assertEquals(SeatStatus.LOCKED, seat.getStatus());
        verify(seatLockRepository, times(1)).save(any(SeatLock.class));
    }

    @Test
    void lockSeat_Conflict() {
        Seat seat = new Seat(1L, 1L, 1, SeatStatus.SOLD);
        when(seatRepository.findByShowtimeIdAndSeatNumber(1L, 1)).thenReturn(Optional.of(seat));

        assertThrows(ResponseStatusException.class, () -> seatService.lockSeat(1L, 1, 100L));
    }
}
