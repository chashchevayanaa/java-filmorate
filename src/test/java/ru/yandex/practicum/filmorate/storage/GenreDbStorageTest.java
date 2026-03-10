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
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class, FilmDbStorage.class, FilmRowMapper.class,
        MpaDbStorage.class, MpaRowMapper.class})
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM genre_films");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM genres");
        jdbcTemplate.execute("ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1");
        jdbcTemplate.execute("INSERT INTO genres (name) VALUES ('Комедия')");
        jdbcTemplate.execute("INSERT INTO genres (name) VALUES ('Драма')");
    }

    @Test
    void getAll_ShouldReturnAllGenres() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(2)
                .extracting(Genre::getName)
                .containsExactly("Комедия", "Драма");
    }

    @Test
    void getById_ShouldReturnGenre_WhenExists() {
        Optional<Genre> genre = genreStorage.getById(1L);
        assertThat(genre).isPresent()
                .hasValueSatisfying(g -> {
                    assertThat(g.getId()).isEqualTo(1L);
                    assertThat(g.getName()).isEqualTo("Комедия");
                });
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        assertThatThrownBy(() -> genreStorage.getById(999L))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    void getByFilmId_ShouldReturnGenresOfFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        Film created = filmStorage.add(film).orElseThrow();

        genreStorage.addFilmGenre(created);
        List<Genre> genres = genreStorage.getByFilmId(created.getId());
        assertThat(genres).isEmpty();

        created.setGenres(List.of(genreStorage.getById(1L).get(), genreStorage.getById(2L).get()));
        genreStorage.addFilmGenre(created);

        genres = genreStorage.getByFilmId(created.getId());
        assertThat(genres).hasSize(2)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void addFilmGenre_ShouldReplaceOldGenres() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        Film created = filmStorage.add(film).orElseThrow();

        created.setGenres(List.of(genreStorage.getById(1L).get()));
        genreStorage.addFilmGenre(created);

        List<Genre> genres = genreStorage.getByFilmId(created.getId());
        assertThat(genres).hasSize(1).extracting(Genre::getId).containsExactly(1L);

        created.setGenres(List.of(genreStorage.getById(2L).get()));
        genreStorage.addFilmGenre(created);

        genres = genreStorage.getByFilmId(created.getId());
        assertThat(genres).hasSize(1).extracting(Genre::getId).containsExactly(2L);
    }

    @Test
    void addFilmGenre_ShouldThrowNotFoundException_WhenGenreDoesNotExist() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        Film created = filmStorage.add(film).orElseThrow();

        Genre fakeGenre = new Genre();
        fakeGenre.setId(999L);
        created.setGenres(List.of(fakeGenre));

        assertThatThrownBy(() -> genreStorage.addFilmGenre(created))
                .isInstanceOf(ru.yandex.practicum.filmorate.exception.NotFoundException.class);
    }
}