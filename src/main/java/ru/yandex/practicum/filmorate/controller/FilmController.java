package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<List<Film>> getAll() {
        log.info("GET /films");
        return new ResponseEntity<>(filmService.getAll(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getById(@PathVariable Long id) {
        log.info("GET /films/{}", id);
        return new ResponseEntity<>(filmService.getById(id), HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<Film> add(@RequestBody Film film) {
        log.info("POST /films - {}", film);
        return new ResponseEntity<>(filmService.add(film), HttpStatusCode.valueOf(200));
    }

    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film film) {
        log.info("PUT /films - {}", film);
        return new ResponseEntity<>(filmService.update(film), HttpStatusCode.valueOf(200));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /films/{}/like/{}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("GET /films/popular?count={}", count);
        return new ResponseEntity<>(filmService.getPopular(count), HttpStatusCode.valueOf(200));
    }
}