package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenresStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class CommonDataService {
    private final MpaStorage mpaStorage;
    private final GenresStorage genresStorage;

    public CommonDataService(MpaStorage mpaStorage, GenresStorage genresStorage) {
        this.mpaStorage = mpaStorage;
        this.genresStorage = genresStorage;
    }

    public Mpa getMpa(Integer id) {
        return mpaStorage.getMpa(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Genre getGenre(Integer id) {
        return genresStorage.getGenre(id);
    }

    public List<Genre> getAllGenre() {
        return genresStorage.getAllGenre();
    }
}
