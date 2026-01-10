package com.cinema.seatservice.controller;

import com.cinema.seatservice.dto.LockRequest;
import com.cinema.seatservice.dto.SeatRequest;
import com.cinema.seatservice.model.Seat;
import com.cinema.seatservice.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/init/{showtimeId}")
    public ResponseEntity<List<Seat>> initSeats(@PathVariable Long showtimeId, @RequestParam(defaultValue = "50") int count) {
        return ResponseEntity.ok(seatService.initSeats(showtimeId, count));
    }

    @GetMapping("/{showtimeId}")
    public ResponseEntity<List<Seat>> getSeats(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(seatService.getSeats(showtimeId));
    }

    @PostMapping("/{showtimeId}/lock")
    public ResponseEntity<Void> lockSeat(@PathVariable Long showtimeId, @RequestBody LockRequest request) {
        seatService.lockSeat(showtimeId, request.getSeatNumber(), request.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{showtimeId}/confirm")
    public ResponseEntity<Void> confirmSeat(@PathVariable Long showtimeId, @RequestBody SeatRequest request) {
        seatService.confirmSeat(showtimeId, request.getSeatNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{showtimeId}/release")
    public ResponseEntity<Void> releaseSeat(@PathVariable Long showtimeId, @RequestBody SeatRequest request) {
        seatService.releaseSeat(showtimeId, request.getSeatNumber());
        return ResponseEntity.ok().build();
    }
}
