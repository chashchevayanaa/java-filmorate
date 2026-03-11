package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.dto.mapper.MpaDTOMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MpaController.class)
@Import(MpaDTOMapper.class)
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaStorage mpaStorage;

    private Mpa mpa;
    private MpaDTO mpaDTO;

    @BeforeEach
    void setUp() {
        mpa = new Mpa(1L, "G", "General");
        mpaDTO = new MpaDTO(1L, "G", "General");
    }

    @Test
    void getAll_ShouldReturn200AndList() throws Exception {
        when(mpaStorage.getAll()).thenReturn(List.of(mpa));

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("G"));
    }

    @Test
    void getById_ShouldReturn200AndMpaDTO_WhenExists() throws Exception {
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