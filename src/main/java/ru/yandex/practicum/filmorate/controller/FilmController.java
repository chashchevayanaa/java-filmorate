package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.mapper.FilmDTOMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final FilmDTOMapper filmDTOMapper;

    @GetMapping
    public ResponseEntity<List<FilmDTO>> getAll() {
        return new ResponseEntity<>(filmService.getAll()
                .stream()
                .map(filmDTOMapper::toDto)
                .toList(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<FilmDTO>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(filmService.getById(id).map(filmDTOMapper::toDto), HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<Optional<FilmDTO>> add(@RequestBody FilmDTO filmDTO) {
        Film film = filmDTOMapper.fromDTO(filmDTO);
        try {
            return new ResponseEntity<>(filmService.add(film).map(filmDTOMapper::toDto), HttpStatusCode.valueOf(200));
        } catch (ValidationException e) {
            return new ResponseEntity<>(Optional.of(filmDTO), HttpStatusCode.valueOf(400));
        } catch (Exception e) {
            return new ResponseEntity<>(Optional.of(filmDTO), HttpStatusCode.valueOf(404));
        }
    }

    @PutMapping
    public ResponseEntity<Optional<FilmDTO>> update(@RequestBody FilmDTO filmDTO) {
        Film film = filmDTOMapper.fromDTO(filmDTO);
        try {
            return new ResponseEntity<>(filmService.update(film).map(filmDTOMapper::toDto), HttpStatusCode.valueOf(200));
        } catch (ValidationException e) {
            return new ResponseEntity<>(Optional.of(filmDTO), HttpStatusCode.valueOf(400));
        } catch (Exception e) {
            return new ResponseEntity<>(Optional.of(filmDTO), HttpStatusCode.valueOf(404));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDTO>> getPopular(@RequestParam(defaultValue = "10") int count) {
        return new ResponseEntity<>(filmService.getPopular(count)
                .stream()
                .map(filmDTOMapper::toDto)
                .toList(), HttpStatusCode.valueOf(200));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }
}