package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("POST /films – добавление фильма: {}", film);
        validateFilm(film);
        long newId = getNextId();
        film.setId(newId);
        films.put(newId, film);
        log.info("Фильм добавлен: id={}", film.getId());
        return film;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("GET /films – запрос списка всех фильмов, всего: {}", films.size());
        return films.values();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("PUT /films – обновление фильма: {}", film);
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id={} не найден", film.getId());
            throw new ValidationException("Фильм с id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлён: id={}", film.getId());
        return film;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Валидация не пройдена: название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Валидация не пройдена: описание длиннее 200 символов");
            throw new ValidationException("Максимальная длина описания фильма — 200 символов");

        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Валидация не пройдена: дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Валидация не пройдена: продолжительность фильма <= 0");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");

        }
    }
}
