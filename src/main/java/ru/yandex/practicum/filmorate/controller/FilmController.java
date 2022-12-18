package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.controller.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.exception.FilmStrorageError;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/films")
public class FilmController {
    private final ConversionService conversionService;
    private final FilmMapper filmMapper;
    private final FilmService service;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public FilmController(ConversionService conversionService, FilmMapper filmMapper, FilmService service) {
        this.conversionService = conversionService;
        this.filmMapper = filmMapper;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody @Valid @NotNull FilmRequestDto dto) {
        Film film = service.addFilm(dto);
        log.info("Добавлен новый фильм: {}", film.toString());
        return new ResponseEntity<Film>(film, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody @Valid @NotNull FilmRequestDto dto) {
        Film film = service.updateFilm(dto);
        log.info("Обновление данных фильма: {}", film.toString());
        return new ResponseEntity<Film>(film, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return new ResponseEntity<>(service.getFilms(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(service.getFilm(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopular(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        List<Film> films = service.getFilms().stream()
                .sorted((p0, p1) ->
                        p1.getLikes().size() - p0.getLikes().size()
                )
                .limit(count)
                .collect(Collectors.toList());
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @DeleteMapping(value = "/clear-for-test")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilms() {
        List<Integer> collect = service.getFilms().stream().map(film -> film.getId()).collect(Collectors.toList());
        for (Integer id : collect) {
            try {
                service.deleteFilm(id);
            } catch (FilmStrorageError e) {
            }
        }
    }
}