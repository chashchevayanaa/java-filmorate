package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.dto.mapper.FilmDTOMapper;
import ru.yandex.practicum.filmorate.dto.mapper.GenreDTOMapper;
import ru.yandex.practicum.filmorate.dto.mapper.MpaDTOMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@Import({FilmDTOMapper.class, GenreDTOMapper.class, MpaDTOMapper.class})
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    private Film testFilm;
    private FilmDTO testFilmDTO;

    @BeforeEach
    void setUp() {
        Mpa mpa = new Mpa(1L, "G", "General");
        testFilm = new Film(1L, "Test Film", "Description", LocalDate.of(2000, 1, 1), 120, List.of(), mpa);
        testFilmDTO = new FilmDTO(1L, "Test Film", "Description", LocalDate.of(2000, 1, 1), 120, List.of(), new MpaDTO(1L, "G", "General"));
    }

    @Test
    void getAll_ShouldReturn200AndList() throws Exception {
        when(filmService.getAll()).thenReturn(List.of(testFilm));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Film"))
                .andExpect(jsonPath("$[0].mpa.id").value(1L));
    }

    @Test
    void getById_ShouldReturn200AndFilmDTO_WhenExists() throws Exception {
        when(filmService.getById(1L)).thenReturn(Optional.of(testFilm));

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Film"));
    }


    @Test
    void add_ShouldReturn200AndFilmDTO_WhenValid() throws Exception {
        FilmDTO inputDTO = new FilmDTO(null, "New Film", "New Desc", LocalDate.of(2010, 1, 1), 100, List.of(), new MpaDTO(1L, "G", "General"));
        Film inputFilm = new Film(null, "New Film", "New Desc", LocalDate.of(2010, 1, 1), 100, List.of(), new Mpa(1L, "G", "General"));
        Film savedFilm = new Film(2L, "New Film", "New Desc", LocalDate.of(2010, 1, 1), 100, List.of(), new Mpa(1L, "G", "General"));
        when(filmService.add(any(Film.class))).thenReturn(Optional.of(savedFilm));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("New Film"));
    }

    @Test
    void add_ShouldReturn400_WhenValidationException() throws Exception {
        FilmDTO inputDTO = new FilmDTO(null, "", "Desc", LocalDate.now(), 100, List.of(), new MpaDTO(1L, "G", "General"));
        when(filmService.add(any(Film.class))).thenThrow(new ValidationException("Invalid"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").doesNotExist()); // возвращается Optional с исходным DTO
    }

    @Test
    void add_ShouldReturn404_WhenOtherException() throws Exception {
        FilmDTO inputDTO = new FilmDTO(null, "New Film", "Desc", LocalDate.now(), 100, List.of(), new MpaDTO(1L, "G", "General"));
        when(filmService.add(any(Film.class))).thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void update_ShouldReturn200AndFilmDTO_WhenValid() throws Exception {
        FilmDTO inputDTO = new FilmDTO(1L, "Updated", "Updated Desc", LocalDate.of(2000, 1, 1), 120, List.of(), new MpaDTO(1L, "G", "General"));
        Film updatedFilm = new Film(1L, "Updated", "Updated Desc", LocalDate.of(2000, 1, 1), 120, List.of(), new Mpa(1L, "G", "General"));
        when(filmService.update(any(Film.class))).thenReturn(Optional.of(updatedFilm));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void update_ShouldReturn400_WhenValidationException() throws Exception {
        FilmDTO inputDTO = new FilmDTO(1L, "", "Desc", LocalDate.now(), 100, List.of(), new MpaDTO(1L, "G", "General"));
        when(filmService.update(any(Film.class))).thenThrow(new ValidationException("Invalid"));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturn404_WhenOtherException() throws Exception {
        FilmDTO inputDTO = new FilmDTO(1L, "Updated", "Desc", LocalDate.now(), 100, List.of(), new MpaDTO(1L, "G", "General"));
        when(filmService.update(any(Film.class))).thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getPopular_ShouldReturn200AndList() throws Exception {
        when(filmService.getPopular(5)).thenReturn(List.of(testFilm));

        mockMvc.perform(get("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getPopular_ShouldUseDefaultCount() throws Exception {
        when(filmService.getPopular(10)).thenReturn(List.of());

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk());
    }

    @Test
    void addLike_ShouldReturn200() throws Exception {
        doNothing().when(filmService).addLike(1L, 2L);
        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());
    }

    @Test
    void removeLike_ShouldReturn200() throws Exception {
        doNothing().when(filmService).removeLike(1L, 2L);
        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());
    }
}