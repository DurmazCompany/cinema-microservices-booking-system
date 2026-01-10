package com.cinema.seatservice.controller;

import com.cinema.seatservice.dto.LockRequest;
import com.cinema.seatservice.dto.SeatRequest;
import com.cinema.seatservice.model.Seat;
import com.cinema.seatservice.model.SeatStatus;
import com.cinema.seatservice.service.SeatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SeatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeatService seatService;

    @Test
    @WithMockUser
    void shouldReturnSeats() throws Exception {
        when(seatService.getSeats(1L)).thenReturn(List.of(new Seat(1L, 1L, 1, SeatStatus.AVAILABLE)));

        mockMvc.perform(get("/seats/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldInitSeats() throws Exception {
        mockMvc.perform(post("/seats/init/1?count=10"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser
    void shouldLockSeat() throws Exception {
        LockRequest request = new LockRequest();
        request.setSeatNumber(1);
        request.setUserId(100L);

        mockMvc.perform(post("/seats/1/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
