package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Film validFilm;
    private final FilmService filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());

    @BeforeEach
    void setUp() {
        filmController = new FilmController(filmService);
        validFilm = new Film();
        validFilm.setName("Название");
        validFilm.setDescription("Отличный фильм");
        validFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        validFilm.setDuration(148);
    }

    @Test
    void shouldAddValidFilm() {
        Film added = filmController.add(validFilm);
        assertNotNull(added.getId());
        assertEquals(1, added.getId());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        validFilm.setName("");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.add(validFilm));
        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        validFilm.setName(null);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.add(validFilm));
        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsOnlySpaces() {
        validFilm.setName("   ");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.add(validFilm));
        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDescriptionTooLong() {
        String longDesc = "a".repeat(201);
        validFilm.setDescription(longDesc);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.add(validFilm));
        assertEquals("Максимальная длина описания фильма — 200 символов", ex.getMessage());
    }

    @Test
    void shouldNotThrowWhenDescriptionExactly200() {
        validFilm.setDescription("a".repeat(200));
        assertDoesNotThrow(() -> filmController.add(validFilm));
    }

    @Test
    void shouldThrowWhenReleaseDateBeforeMin() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.add(validFilm));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    void shouldNotThrowWhenReleaseDateIsMin() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> filmController.add(validFilm));
    }

    @Test
    void shouldThrowWhenDurationIsZero() {
        validFilm.setDuration(0);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.add(validFilm));
        assertEquals("Продолжительность фильма должна быть положительным числом", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDurationIsNegative() {
        validFilm.setDuration(-10);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.add(validFilm));
        assertEquals("Продолжительность фильма должна быть положительным числом", ex.getMessage());
    }

    @Test
    void shouldUpdateValidFilm() {
        Film added = filmController.add(validFilm);
        added.setName("Updated");
        Film updated = filmController.update(added);
        assertEquals("Updated", updated.getName());
    }

    @Test
    void shouldThrowWhenUpdateWithInvalidFilm() {
        Film added = filmController.add(validFilm);
        added.setName("");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.update(added));
        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }
}