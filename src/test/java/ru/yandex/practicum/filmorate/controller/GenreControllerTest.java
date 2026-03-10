package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreStorage genreStorage;

    @Test
    void getAll_ShouldReturn200AndList() throws Exception {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Комедия");
        when(genreStorage.getAll()).thenReturn(List.of(genre));

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Комедия"));
    }

    @Test
    void getById_ShouldReturn200AndOptionalGenre_WhenExists() throws Exception {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Комедия");
        when(genreStorage.getById(1L)).thenReturn(Optional.of(genre));

        mockMvc.perform(get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Комедия"));
    }

    @Test
    void getById_ShouldReturn404_WhenNotFound() throws Exception {
        when(genreStorage.getById(99L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/genres/99"))
                .andExpect(status().isNotFound());
    }
}