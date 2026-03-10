package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MpaController.class)
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaStorage mpaStorage;

    @Test
    void getAll_ShouldReturn200AndList() throws Exception {
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        mpa.setDescription("General");
        when(mpaStorage.getAll()).thenReturn(List.of(mpa));

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("G"));
    }

    @Test
    void getById_ShouldReturn200AndOptionalMpa_WhenExists() throws Exception {
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        when(mpaStorage.getById(1L)).thenReturn(Optional.of(mpa));

        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("G"));
    }

    @Test
    void getById_ShouldReturn404_WhenNotFound() throws Exception {
        when(mpaStorage.getById(99L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/mpa/99"))
                .andExpect(status().isNotFound());
    }
}