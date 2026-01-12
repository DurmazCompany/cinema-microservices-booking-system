package com.cinema.bookingservice.service;

import com.cinema.bookingservice.model.Booking;
import com.cinema.bookingservice.model.BookingStatus;
import com.cinema.bookingservice.repository.BookingRepository;
import com.cinema.bookingservice.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    private final PaymentMock paymentMock;

    private static final String CATALOG_SERVICE_URL = "https://catalog-service-msei.onrender.com/showtimes";
    private static final String SEAT_SERVICE_URL = "https://seat-service-1qck.onrender.com/seats";

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional
    public Booking createBooking(Long showtimeId, Integer seatNumber, Long userId) {
        // ... (Catalog and Lock Logic remain same) ...
        // 1. Check if showtime exists (Catalog Service)
        try {
            restTemplate.getForObject(CATALOG_SERVICE_URL + "/" + showtimeId + "/exists", Map.class);
        } catch (Exception e) {
            String msg = "Catalog check failed: " + e.getMessage();
            log.error(msg, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }

        // 2. Lock Seat (Seat Service)
        HttpHeaders headers = new HttpHeaders();
        String token = request.getHeader("Authorization");
        headers.set("Authorization", token);

        Map<String, Object> lockRequest = Map.of("seatNumber", seatNumber, "userId", userId);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(lockRequest, headers);

        try {
            restTemplate.postForEntity(SEAT_SERVICE_URL + "/" + showtimeId + "/lock", entity, Void.class);
        } catch (Exception e) {
            String msg = "Seat locking failed: " + e.getMessage();
            log.error(msg, e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, msg);
        }

        // 3. Process Payment
        boolean paymentSuccess = paymentMock.processPayment(userId, 10.0);

        Booking booking = Booking.builder()
                .userId(userId)
                .showtimeId(showtimeId)
                .seatNumber(seatNumber)
                .createdAt(LocalDateTime.now())
                .status(BookingStatus.PENDING)
                .build();

        bookingRepository.save(booking);

        Map<String, Object> seatActionRequest = Map.of("seatNumber", seatNumber);
        HttpEntity<Map<String, Object>> seatActionEntity = new HttpEntity<>(seatActionRequest, headers);

        if (paymentSuccess) {
            // 4. Confirm Seat
            try {
                restTemplate.postForEntity(SEAT_SERVICE_URL + "/" + showtimeId + "/confirm", seatActionEntity,
                        Void.class);
                booking.setStatus(BookingStatus.CONFIRMED);
            } catch (Exception e) {
                booking.setStatus(BookingStatus.CANCELLED);
                log.error("Failed to confirm seat after payment", e);
            }
        } else {
            // 4. Payment Fail -> Release Seat
            try {
                restTemplate.postForEntity(SEAT_SERVICE_URL + "/" + showtimeId + "/release", seatActionEntity,
                        Void.class);
            } catch (Exception e) {
                log.error("Failed to release seat after payment failure", e);
            }
            booking.setStatus(BookingStatus.CANCELLED);
        }

        return bookingRepository.save(booking);
    }
}
