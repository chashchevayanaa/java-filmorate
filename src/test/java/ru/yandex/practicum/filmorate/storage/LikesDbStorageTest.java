package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({LikesDbStorage.class, FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class,
        MpaDbStorage.class, MpaRowMapper.class,
        UserDbStorage.class, UserRowMapper.class})
class LikesDbStorageTest {

    @Autowired
    private LikesDbStorage likesStorage;

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Film film1, film2, film3;
    private Long userId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM film_likes");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM genre_films");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.execute("MERGE INTO mpa (mpa_id, name, description) VALUES (1, 'G', 'General')");
        jdbcTemplate.execute("MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия')");

        film1 = createFilm("Film1");
        film2 = createFilm("Film2");
        film3 = createFilm("Film3");

        userId = createUser().getId();
    }

    private Film createFilm(String name) {
        Film f = new Film();
        f.setName(name);
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        return filmStorage.add(f).orElseThrow();
    }

    private User createUser() {
        User u = new User();
        u.setEmail("user@mail.ru");
        u.setLogin("userLogin");
        u.setName("User");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        return userStorage.add(u);
    }

    @Test
    void addLike_ShouldInsertLike() {
        likesStorage.addLike(film1.getId(), userId);
        List<Film> popular = likesStorage.getPopularFilms(10);
        assertThat(popular).hasSize(3);
        assertThat(popular.get(0).getId()).isEqualTo(film1.getId());
    }

    @Test
    void removeLike_ShouldDeleteLike() {
        likesStorage.addLike(film1.getId(), userId);
        likesStorage.removeLike(film1.getId(), userId);

        likesStorage.addLike(film2.getId(), userId);
        List<Film> popular = likesStorage.getPopularFilms(10);
        assertThat(popular.get(0).getId()).isEqualTo(film2.getId());
    }

    @Test
    void getPopularFilms_ShouldLimitCount() {
        likesStorage.addLike(film1.getId(), userId);
        likesStorage.addLike(film2.getId(), userId);

        List<Film> popular = likesStorage.getPopularFilms(1);
        assertThat(popular).hasSize(1);
    }
}