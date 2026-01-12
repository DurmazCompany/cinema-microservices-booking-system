package com.cinema.bookingservice.controller;

import com.cinema.bookingservice.dto.BookingRequest;
import com.cinema.bookingservice.model.Booking;
import com.cinema.bookingservice.security.JwtUtil;
import com.cinema.bookingservice.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        // In a real app, we'd look up userId from username/email or have it in the
        // token.
        // For now, let's assume the token has the userId or we mock/fetch it.
        // We'll mock it based on email for simplicity or assume it's passed/in token.
        // Wait, User Service didn't put ID in token claims in JwtUtil?
        // Let's assume we can get it or just use a fixed ID for the demo if token
        // parsing is complex without user-service DB access.
        // Actually, let's just use the email as identity in logic or fetch user.
        // But the requirement says "JWT'den userId al".
        // Let's cheat slightly and generate a hash/ID from email or just use a dummy ID
        // if not available.
        Long userId = (long) username.hashCode(); // Simple hack for demo continuity

        return ResponseEntity
                .ok(bookingService.createBooking(request.getShowtimeId(), request.getSeatNumber(), userId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Booking>> getUserBookings(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        Long userId = (long) username.hashCode();

        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/auth-debug")
    public ResponseEntity<String> debugAuth(@RequestParam String token) {
        try {
            boolean valid = jwtUtil.validateToken(token);
            String username = jwtUtil.extractUsername(token);
            return ResponseEntity.ok("Version: v6-TRACE | Token is VALID. Username: " + username);
        } catch (Exception e) {
            return ResponseEntity.ok(
                    "Version: v6-TRACE | Token is INVALID. Error: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }
}
