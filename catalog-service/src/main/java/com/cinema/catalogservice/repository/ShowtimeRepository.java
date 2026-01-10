package com.cinema.catalogservice.repository;

import com.cinema.catalogservice.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    boolean existsById(Long id);
}
