package com.cinema.bookingservice.service;

import com.cinema.bookingservice.model.Booking;
import com.cinema.bookingservice.model.BookingStatus;
import com.cinema.bookingservice.repository.BookingRepository;
import com.cinema.bookingservice.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PaymentMock paymentMock;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_Success() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.getForObject(contains("catalog-service"), eq(Map.class))).thenReturn(Map.of("exists", true));
        when(restTemplate.postForEntity(contains("lock"), any(), eq(Void.class))).thenReturn(null);
        when(restTemplate.postForEntity(contains("confirm"), any(), eq(Void.class))).thenReturn(null);
        when(paymentMock.processPayment(anyLong(), anyDouble())).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        Booking result = bookingService.createBooking(1L, 1, 100L);

        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void createBooking_PaymentFail() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.getForObject(contains("catalog-service"), eq(Map.class))).thenReturn(Map.of("exists", true));
        when(restTemplate.postForEntity(contains("lock"), any(), eq(Void.class))).thenReturn(null);
        when(restTemplate.postForEntity(contains("release"), any(), eq(Void.class))).thenReturn(null);
        when(paymentMock.processPayment(anyLong(), anyDouble())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        Booking result = bookingService.createBooking(1L, 1, 100L);

        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        verify(restTemplate, times(1)).postForEntity(contains("release"), any(), eq(Void.class));
    }
}
