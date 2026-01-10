package com.cinema.catalogservice.repository;

import com.cinema.catalogservice.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
