package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.mapper.GenreDTOMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreStorage service;
    private final GenreDTOMapper genreDTOMapper;

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAll() {
        return new ResponseEntity<>(service.getAll()
                .stream()
                .map(genreDTOMapper::toDto)
                .toList(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<GenreDTO>> getById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(service.getById(id).map(genreDTOMapper::toDto), HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
    }
}