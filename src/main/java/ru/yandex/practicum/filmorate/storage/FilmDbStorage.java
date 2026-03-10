package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
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

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into films (name, description, duration, releaseDate) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        film.setId(id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.addFilmGenre(film);
        }
        if (film.getMpa() != null) {
            mpaStorage.addFilmMpa(film);
        }

        return getById(id);
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
}
