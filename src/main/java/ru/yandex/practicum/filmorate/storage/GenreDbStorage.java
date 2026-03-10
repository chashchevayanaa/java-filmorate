package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("select * from genres", genreRowMapper);
    }

    @Override
    public Optional<Genre> getById(Long id) {
        String query = "select * from genres where genre_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, genreRowMapper, id));
    }

    @Override
    public List<Genre> getByFilmId(Long id) {
        List<Genre> genreList = new ArrayList<>();
        String query = """
                SELECT
                	g.genre_id,
                	g.name
                FROM
                	genre_films gf
                JOIN genres g ON
                	gf.genre_id = g.genre_id
                WHERE
                	gf.film_id = ?
                """;
        try {
            return jdbcTemplate.query(query, genreRowMapper, id);
        } catch (Exception e) {
            return genreList;
        }
    }

    @Override
    public void addFilmGenre(Film film) throws NotFoundException {
        jdbcTemplate.update("delete from genre_films where film_id = ?", film.getId());
        for (Genre genre : film.getGenres()) {
            if (!dbHasGenre(genre.getId()))
                throw new NotFoundException("");
            jdbcTemplate.update(
                    "insert into genre_films (film_id, genre_id) values (?, ?)",
                    film.getId(), genre.getId());
        }
    }

    public boolean dbHasGenre(Long id) {
        try {
            jdbcTemplate.queryForObject("select * from genres where genre_id = ?", genreRowMapper, id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}