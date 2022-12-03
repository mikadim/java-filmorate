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
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/films")
public class FilmController {
    private List<Film> films = new ArrayList<>();
    private final ConversionService conversionService;
    private final FilmMapper filmMapper;
    private final IdGenerator idGenerator;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public FilmController(ConversionService conversionService, FilmMapper filmMapper, IdGenerator idGenerator) {
        this.conversionService = conversionService;
        this.filmMapper = filmMapper;
        this.idGenerator = idGenerator;
    }

    @PostMapping()
    public ResponseEntity<Film> addFilm(@RequestBody @Valid @NotNull FilmRequestDto dto) {
        dto.setId(idGenerator.getId());
        Film film = conversionService.convert(dto, Film.class);
        films.add(film);
        log.info("Добавлен новый фильм: {}", film.toString());
        return new ResponseEntity<Film>(film, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<Film> updateUser(@RequestBody @Valid @NotNull FilmRequestDto dto) {
        // Film film = filmMapper.mapToFilm(dto);
        Film film = conversionService.convert(dto, Film.class);

        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                log.info("Добавлен новый фильм: {}", film.toString());
                return new ResponseEntity<Film>(film, HttpStatus.OK);
            }
        }
        return new ResponseEntity<Film>(film, HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }
}
