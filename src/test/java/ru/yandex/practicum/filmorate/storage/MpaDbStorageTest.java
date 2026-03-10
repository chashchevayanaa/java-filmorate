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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class, FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class})
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM film_likes");
        jdbcTemplate.execute("DELETE FROM genre_films");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM mpa");
        jdbcTemplate.execute("ALTER TABLE mpa ALTER COLUMN mpa_id RESTART WITH 1");
        // Вставляем предопределённые MPA
        jdbcTemplate.execute("INSERT INTO mpa (name, description) VALUES ('G', 'General')");
        jdbcTemplate.execute("INSERT INTO mpa (name, description) VALUES ('PG', 'Parental Guidance')");
    }

    @Test
    void getAll_ShouldReturnAllMpa() {
        List<Mpa> mpas = mpaStorage.getAll();
        assertThat(mpas).hasSize(2)
                .extracting(Mpa::getName)
                .containsExactly("G", "PG");
    }

    @Test
    void getById_ShouldReturnMpa_WhenExists() {
        Optional<Mpa> mpa = mpaStorage.getById(1L);
        assertThat(mpa).isPresent()
                .hasValueSatisfying(m -> {
                    assertThat(m.getId()).isEqualTo(1L);
                    assertThat(m.getName()).isEqualTo("G");
                });
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        assertThatThrownBy(() -> mpaStorage.getById(999L))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    void getByFilmId_ShouldReturnMpaOfFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        film.setMpa(mpaStorage.getById(1L).orElse(null));
        Film created = filmStorage.add(film).orElseThrow();

        Mpa mpa = mpaStorage.getByFilmId(created.getId());
        assertThat(mpa.getId()).isEqualTo(1L);
    }

    @Test
    void getByFilmId_ShouldReturnEmptyMpaObject_WhenFilmHasNoMpa() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        Film created = filmStorage.add(film).orElseThrow();

        Mpa mpa = mpaStorage.getByFilmId(created.getId());
        assertThat(mpa.getId()).isNull();
        assertThat(mpa.getName()).isNull();
    }

    @Test
    void addFilmMpa_ShouldUpdateMpaForFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        Film created = filmStorage.add(film).orElseThrow();

        Mpa mpa2 = mpaStorage.getById(2L).orElseThrow();
        created.setMpa(mpa2);
        mpaStorage.addFilmMpa(created);

        Mpa mpaFromDb = mpaStorage.getByFilmId(created.getId());
        assertThat(mpaFromDb.getId()).isEqualTo(2L);
    }

    @Test
    void addFilmMpa_ShouldThrowNotFoundException_WhenMpaDoesNotExist() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        Film created = filmStorage.add(film).orElseThrow();

        Mpa fakeMpa = new Mpa();
        fakeMpa.setId(999L);
        created.setMpa(fakeMpa);

        assertThatThrownBy(() -> mpaStorage.addFilmMpa(created))
                .isInstanceOf(ru.yandex.practicum.filmorate.exception.NotFoundException.class);
    }
}