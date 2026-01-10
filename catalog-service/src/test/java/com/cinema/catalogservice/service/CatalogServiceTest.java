package com.cinema.catalogservice.service;

import com.cinema.catalogservice.model.Movie;
import com.cinema.catalogservice.repository.MovieRepository;
import com.cinema.catalogservice.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CatalogServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllMovies() {
        Movie movie = new Movie(1L, "Test Movie", "Desc", 120, "Action");
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<Movie> movies = catalogService.getAllMovies();

        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getTitle());
    }
}
