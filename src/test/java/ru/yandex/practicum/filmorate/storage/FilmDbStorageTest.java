package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, MpaDbStorage.class,
        GenreRowMapper.class, MpaRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private MpaDbStorage mpaStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM film_likes");
        jdbcTemplate.execute("DELETE FROM genre_films");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("MERGE INTO mpa (mpa_id, name, description) VALUES (1, 'G', 'General')");
        jdbcTemplate.execute("MERGE INTO mpa (mpa_id, name, description) VALUES (2, 'PG', 'Parental Guidance')");
        jdbcTemplate.execute("MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия')");
        jdbcTemplate.execute("MERGE INTO genres (genre_id, name) VALUES (2, 'Драма')");
    }

    @Test
    void getAll_ShouldReturnAllFilms() {
        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Desc1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpa(mpaStorage.getById(1L).orElse(null));

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc2");
        film2.setReleaseDate(LocalDate.of(2010, 5, 5));
        film2.setDuration(90);
        film2.setMpa(mpaStorage.getById(2L).orElse(null));

        filmStorage.add(film1);
        filmStorage.add(film2);

        List<Film> films = filmStorage.getAll();
        assertThat(films).hasSize(2)
                .extracting(Film::getName)
                .containsExactlyInAnyOrder("Film1", "Film2");
    }

    @Test
    void getById_ShouldReturnFilm_WhenExists() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 10, 10));
        film.setDuration(150);
        film.setMpa(mpaStorage.getById(1L).orElse(null));
        film.setGenres(List.of(genreStorage.getById(1L).get()));

        Optional<Film> added = filmStorage.add(film);
        Long filmId = added.get().getId();

        Optional<Film> found = filmStorage.getById(filmId);
        assertThat(found).isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getId()).isEqualTo(filmId);
                    assertThat(f.getName()).isEqualTo("Test Film");
                    assertThat(f.getMpa().getId()).isEqualTo(1);
                    assertThat(f.getGenres()).hasSize(1);
                });
    }

    @Test
    void getById_ShouldThrowDataIntegrityViolation_WhenNotExists() {
        assertThatThrownBy(() -> filmStorage.getById(999L))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    void add_ShouldInsertFilmWithMpaAndGenres() {
        Film film = new Film();
        film.setName("New Film");
        film.setDescription("New Desc");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(100);
        Mpa mpa = mpaStorage.getById(2L).orElseThrow();
        film.setMpa(mpa);
        Genre genre1 = genreStorage.getById(1L).get();
        Genre genre2 = genreStorage.getById(2L).get();
        film.setGenres(List.of(genre1, genre2));

        Optional<Film> insertedOpt = filmStorage.add(film);
        assertThat(insertedOpt).isPresent();
        Film inserted = insertedOpt.get();

        assertThat(inserted.getId()).isNotNull();
        assertThat(inserted.getMpa().getId()).isEqualTo(2);
        assertThat(inserted.getGenres()).hasSize(2)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void update_ShouldModifyExistingFilm() {
        Film film = new Film();
        film.setName("Original");
        film.setDescription("Original desc");
        film.setReleaseDate(LocalDate.of(2005, 5, 5));
        film.setDuration(110);
        film.setMpa(mpaStorage.getById(1L).orElse(null));
        film.setGenres(List.of(genreStorage.getById(1L).get()));

        Optional<Film> added = filmStorage.add(film);
        Film toUpdate = added.get();
        toUpdate.setName("Updated");
        toUpdate.setDescription("Updated desc");
        toUpdate.setMpa(mpaStorage.getById(2L).orElse(null));
        toUpdate.setGenres(List.of(genreStorage.getById(2L).get()));

        Optional<Film> updatedOpt = filmStorage.update(toUpdate);
        assertThat(updatedOpt).isPresent();
        Film updated = updatedOpt.get();

        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getMpa().getId()).isEqualTo(2);
        assertThat(updated.getGenres()).hasSize(1)
                .extracting(Genre::getId)
                .containsExactly(2L);
    }
}