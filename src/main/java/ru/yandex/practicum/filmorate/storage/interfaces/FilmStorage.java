package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getAll();

    Optional<Film> getById(Long id);

    Optional<Film> add(Film film);

    Optional<Film> update(Film film);

}