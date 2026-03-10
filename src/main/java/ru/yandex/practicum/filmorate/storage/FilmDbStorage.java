package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FilmRowMapper filmRowMapper;
    @Autowired
    private GenreStorage genreStorage;
    @Autowired
    private MpaStorage mpaStorage;

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbcTemplate.query("select * from films", filmRowMapper);
        for (Film film : films) {
            film.setGenres(genreStorage.getByFilmId(film.getId()));
            film.setMpa(mpaStorage.getByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public Optional<Film> getById(Long id) {
        String query = "select * from films where id = ?";
        Film film = jdbcTemplate.queryForObject(query, filmRowMapper, id);
        assert film != null;
        film.setGenres(genreStorage.getByFilmId(film.getId()));
        film.setMpa(mpaStorage.getByFilmId(film.getId()));
        return Optional.of(film);
    }

    @Override
    public Optional<Film> add(Film film) {
        jdbcTemplate.update(
                "insert into films (name, description, duration, releaseDate) values (?, ?, ?, ?)",
                film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate());
        Optional<Film> insertedFilm = getByNameAndReleaseDate(film.getName(), film.getReleaseDate());
        insertedFilm.get().setGenres(film.getGenres());
        if (film.getGenres() != null)
            genreStorage.addFilmGenre(insertedFilm.get());
        insertedFilm.get().setMpa(film.getMpa());
        if (film.getMpa() != null)
            mpaStorage.addFilmMpa(insertedFilm.get());

        return getByNameAndReleaseDate(film.getName(), film.getReleaseDate());
    }

    @Override
    public Optional<Film> update(Film film) {
        jdbcTemplate.update(
                "update films set name = ?, description = ?, duration = ?, releaseDate = ? where id = ?",
                film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(), film.getId());
        if (film.getGenres() != null)
            genreStorage.addFilmGenre(film);
        if (film.getMpa() != null)
            mpaStorage.addFilmMpa(film);
        return getById(film.getId());
    }

    private Optional<Film> getByNameAndReleaseDate(String name, LocalDate releaseDate) {
        String query = "select * from films where name = ? and releaseDate = ?";
        Film film = jdbcTemplate.queryForObject(query, filmRowMapper, name, releaseDate);
        assert film != null;
        try {
            film.setGenres(genreStorage.getByFilmId(film.getId()));
        } catch (Exception e) {
            film.setGenres(new ArrayList<>());
        }

        try {
            film.setMpa(mpaStorage.getByFilmId(film.getId()));
        } catch (Exception e) {
            film.setMpa(new Mpa());
        }
        return Optional.of(film);
    }
}
