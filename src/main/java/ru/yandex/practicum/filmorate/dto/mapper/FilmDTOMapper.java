package ru.yandex.practicum.filmorate.dto.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class FilmDTOMapper {

    private final GenreDTOMapper genreDTOMapper;
    private final MpaDTOMapper mpaDTOMapper;

    public FilmDTO toDto(Film film) {
        List<GenreDTO> genreDTOList = new ArrayList<>();
        MpaDTO mpaDTO = new MpaDTO();

        if (film.getGenres() != null)
            genreDTOList = film.getGenres().stream().map(genreDTOMapper::toDto).toList();

        if (film.getMpa() != null)
            mpaDTO = mpaDTOMapper.toDto(film.getMpa());

        return new FilmDTO(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), genreDTOList, mpaDTO);
    }

    public Film fromDTO(FilmDTO filmDTO) {
        List<Genre> genreList = new ArrayList<>();
        Mpa mpa = new Mpa();

        if (filmDTO.getGenres() != null)
            genreList = filmDTO.getGenres().stream().map(genreDTOMapper::fromDto).toList();

        if (filmDTO.getMpa() != null)
            mpa = mpaDTOMapper.fromDto(filmDTO.getMpa());

        return new Film(filmDTO.getId(), filmDTO.getName(), filmDTO.getDescription(), filmDTO.getReleaseDate(), filmDTO.getDuration(), genreList, mpa);
    }
}
