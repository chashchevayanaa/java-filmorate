package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MpaRowMapper mapper;

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query("select * from mpa", mapper);
    }

    @Override
    public Optional<Mpa> getById(Long id) {
        String query = "select * from mpa where mpa_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, mapper, id));
    }

    @Override
    public Mpa getByFilmId(Long id) {
        Mpa mpa = new Mpa();
        String query = """
                SELECT
                	m.mpa_id,
                	m.name,
                	m.description
                FROM
                	films f
                JOIN mpa m ON
                	f.mpa_id = m.mpa_id
                WHERE
                	f.id = ?
                """;
        try {
            return jdbcTemplate.queryForObject(query, mapper, id);
        } catch (Exception e) {
            return mpa;
        }
    }

    @Override
    public void addFilmMpa(Film film) {
        if (!dbHasMpa(film.getMpa().getId()))
            throw new NotFoundException("");
        jdbcTemplate.update("update films set mpa_id = ? where id = ?", film.getMpa().getId(), film.getId());
    }

    public boolean dbHasMpa(Long id) {
        Long count = jdbcTemplate.queryForObject("select count(*) from mpa where mpa_id = ?", Long.class, id);
        return count > 0;
    }
}
