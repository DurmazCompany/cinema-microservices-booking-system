package com.cinema.catalogservice.controller;

import com.cinema.catalogservice.model.Showtime;
import com.cinema.catalogservice.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final CatalogService catalogService;

    @GetMapping
    public ResponseEntity<List<Showtime>> getAllShowtimes() {
        return ResponseEntity.ok(catalogService.getAllShowtimes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getShowtimeById(id));
    }

    @PostMapping
    public ResponseEntity<Showtime> createShowtime(@RequestBody @Valid Showtime showtime) {
        return ResponseEntity.ok(catalogService.createShowtime(showtime));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> checkShowtimeExists(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("exists", catalogService.showtimeExists(id)));
    }

    @GetMapping("/test-deploy")
    public ResponseEntity<String> testDeploy() {
        return ResponseEntity.ok("Deployment Working!");
    }
}
