package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.dto.mapper.MpaDTOMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaStorage service;
    private final MpaDTOMapper mpaDTOMapper;

    @GetMapping
    public ResponseEntity<List<MpaDTO>> getAll() {
        return new ResponseEntity<>(service.getAll()
                .stream()
                .map(mpaDTOMapper::toDto)
                .toList(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<MpaDTO>> getById(@PathVariable Long id) {
        Optional<Mpa> mpa;
        try {
            mpa = service.getById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
        return new ResponseEntity<>(mpa.map(mpaDTOMapper::toDto), HttpStatusCode.valueOf(200));
    }
}
