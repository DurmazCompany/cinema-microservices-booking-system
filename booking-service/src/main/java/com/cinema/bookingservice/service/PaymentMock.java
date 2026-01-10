package com.cinema.bookingservice.service;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class PaymentMock {
    public boolean processPayment(Long userId, double amount) {
        // 90% success
        return new Random().nextInt(100) < 90;
    }
}
