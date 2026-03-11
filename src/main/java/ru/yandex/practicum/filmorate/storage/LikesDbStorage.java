package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikesStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;

@Slf4j
@Component
public class LikesDbStorage implements LikesStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FilmRowMapper filmRowMapper;
    @Autowired
    private GenreStorage genreStorage;
    @Autowired
    private MpaStorage mpaStorage;

    @Override
    public void addLike(long filmId, long userId) {
        String query = "insert into film_likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(query, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String query = "delete from film_likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(query, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String query = """
                SELECT
                	f.*
                FROM
                	films f
                LEFT JOIN (
                	SELECT
                		film_id,
                		count(1) pop
                	FROM
                		film_likes
                	GROUP BY
                		film_id) fp ON
                	f.id = fp.film_id
                ORDER BY
                	pop DESC NULLS LAST
                LIMIT ?
                """;
        List<Film> films = jdbcTemplate.query(query, filmRowMapper, count);
        for (Film film : films) {
            film.setGenres(genreStorage.getByFilmId(film.getId()));
            film.setMpa(mpaStorage.getByFilmId(film.getId()));
        }
        return films;
    }
}
