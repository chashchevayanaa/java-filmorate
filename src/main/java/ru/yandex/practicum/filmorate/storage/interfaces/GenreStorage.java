package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> getAll();

    Optional<Genre> getById(Long id);

    List<Genre> getByFilmId(Long id);

    void addFilmGenre(Film film);
}
