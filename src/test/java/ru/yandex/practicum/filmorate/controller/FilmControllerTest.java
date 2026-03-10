package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    @Test
    void getAll_ShouldReturn200AndList() throws Exception {
        Film film = createTestFilm(1L);
        when(filmService.getAll()).thenReturn(List.of(film));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Film"));
    }

    @Test
    void getById_ShouldReturn200AndFilm_WhenExists() throws Exception {
        Film film = createTestFilm(1L);
        when(filmService.getById(1L)).thenReturn(film);

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_ShouldReturn404_WhenNotFound() throws Exception {
        when(filmService.getById(99L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/films/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found"));
    }

    @Test
    void add_ShouldReturn200AndOptionalFilm_WhenValid() throws Exception {
        Film film = createTestFilm(null);
        Film createdFilm = createTestFilm(1L);
        when(filmService.add(any(Film.class))).thenReturn(Optional.of(createdFilm));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void add_ShouldReturn404_WhenOtherException() throws Exception {
        Film film = createTestFilm(null);
        when(filmService.add(any(Film.class))).thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldReturn200AndOptionalFilm_WhenValid() throws Exception {
        Film film = createTestFilm(1L);
        when(filmService.update(any(Film.class))).thenReturn(Optional.of(film));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturn400_WhenValidationException() throws Exception {
        Film film = createTestFilm(1L);
        when(filmService.update(any(Film.class))).thenThrow(new ValidationException("Invalid"));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn404_WhenOtherException() throws Exception {
        Film film = createTestFilm(1L);
        when(filmService.update(any(Film.class))).thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPopular_ShouldReturn200AndList() throws Exception {
        Film film = createTestFilm(1L);
        when(filmService.getPopular(5)).thenReturn(List.of(film));

        mockMvc.perform(get("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getPopular_ShouldUseDefaultCount() throws Exception {
        Film film = createTestFilm(1L);
        when(filmService.getPopular(10)).thenReturn(List.of(film));

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk());
    }

    @Test
    void addLike_ShouldReturn200() throws Exception {
        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());
    }

    @Test
    void removeLike_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());
    }

    private Film createTestFilm(Long id) {
        Film film = new Film();
        film.setId(id);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);
        return film;
    }
}