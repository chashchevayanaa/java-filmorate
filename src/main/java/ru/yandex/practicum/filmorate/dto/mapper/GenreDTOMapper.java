package ru.yandex.practicum.filmorate.dto.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class GenreDTOMapper {

    public GenreDTO toDto(Genre genre) {

        return new GenreDTO(genre.getId(), genre.getName());
    }

    public Genre fromDto(GenreDTO genreDTO) {

        return new Genre(genreDTO.getId(), genreDTO.getName());
    }
}
