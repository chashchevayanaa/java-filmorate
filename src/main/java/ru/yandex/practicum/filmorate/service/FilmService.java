package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage; // для проверки существования пользователя
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public List<Film> getAll() {
        log.debug("Запрос списка всех фильмов");
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        log.debug("Запрос фильма по id: {}", id);
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public Film add(Film film) {
        log.debug("Добавление нового фильма: {}", film);
        validate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        log.debug("Обновление фильма: {}", film);
        validate(film);
        getById(film.getId()); // проверка существования
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Пользователь {} ставит лайк фильму {}", userId, filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Film film = getById(filmId);
        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Film film = getById(filmId);
        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<Film> getPopular(int count) {
        log.debug("Запрос популярных фильмов, количество: {}", count);
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        log.debug("Валидация фильма: {}", film);
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания фильма — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
