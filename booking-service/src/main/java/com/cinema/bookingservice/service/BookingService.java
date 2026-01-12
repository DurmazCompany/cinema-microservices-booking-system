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

    // Use internal Render URLs with correct ports (defined in render.yaml)
    // Catalog Service: 8082, Seat Service: 8083
    private static final String CATALOG_SERVICE_URL = "http://catalog-service:8082/showtimes";
    private static final String SEAT_SERVICE_URL = "http://seat-service:8083/seats";

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional
    public Booking createBooking(Long showtimeId, Integer seatNumber, Long userId) {
        // ... (Catalog and Lock Logic remain same) ...

        HttpHeaders headers = new HttpHeaders();
        String token = request.getHeader("Authorization");
        headers.set("Authorization", token);
        HttpEntity<Void> headersEntity = new HttpEntity<>(headers);

        // 1. Check if showtime exists (Catalog Service)
        try {
            // Fix: Add JWT token to Catalog Service call just in case
            // Fix: Verify the boolean result!
            ResponseEntity<Map> response = restTemplate.exchange(
                    CATALOG_SERVICE_URL + "/" + showtimeId + "/exists",
                    HttpMethod.GET,
                    headersEntity,
                    Map.class);

            Map<String, Boolean> result = response.getBody();
            if (result == null || !Boolean.TRUE.equals(result.get("exists"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Showtime ID " + showtimeId + " not found in Catalog");
            }
        } catch (Exception e) {
            String validationError = "Catalog check failed for ID " + showtimeId + ": " + e.getMessage();
            System.err.println(validationError);
            // If it's already a ResponseStatusException (manual throw above), rethrow it
            if (e instanceof ResponseStatusException) {
                throw (ResponseStatusException) e;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationError);
        }

        // 2. Lock Seat (Seat Service)
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
