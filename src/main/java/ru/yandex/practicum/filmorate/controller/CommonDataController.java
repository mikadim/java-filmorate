package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.CommonDataService;

import java.util.List;

@RestController
public class CommonDataController {
    private final CommonDataService service;

    public CommonDataController(CommonDataService service) {
        this.service = service;
    }

    @GetMapping("/mpa/{id}")
    public ResponseEntity<Mpa> getMpa(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(service.getMpa(id), HttpStatus.OK);
    }

    @GetMapping("/mpa")
    public ResponseEntity<List<Mpa>> getAllMpa() {
        return new ResponseEntity<>(service.getAllMpa(), HttpStatus.OK);
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(service.getGenre(id), HttpStatus.OK);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenre() {
        return new ResponseEntity<>(service.getAllGenre(), HttpStatus.OK);
    }
}
