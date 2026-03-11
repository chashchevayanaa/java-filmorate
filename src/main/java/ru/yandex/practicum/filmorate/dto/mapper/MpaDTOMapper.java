package ru.yandex.practicum.filmorate.dto.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaDTOMapper {

    public MpaDTO toDto(Mpa mpa) {

        return new MpaDTO(mpa.getId(), mpa.getName(), mpa.getDescription());
    }

    public Mpa fromDto(MpaDTO mpaDTO) {

        return new Mpa(mpaDTO.getId(), mpaDTO.getName(), mpaDTO.getDescription());
    }
}
