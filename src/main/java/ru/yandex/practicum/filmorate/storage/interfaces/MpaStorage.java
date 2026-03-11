package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    List<Mpa> getAll();

    Optional<Mpa> getById(Long id);

    Mpa getByFilmId(Long id);

    void addFilmMpa(Film film);
}