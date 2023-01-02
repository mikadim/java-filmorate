package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public FilmService(@Qualifier("dbStorage") FilmStorage filmStorage, @Qualifier("dbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer id, Integer userId) {
        if (filmStorage.getFilm(id) != null && userStorage.getUser(userId) != null) {
            filmStorage.addLike(id, userId);
        }
    }

    public void deleteLike(Integer id, Integer userId) {
        if (filmStorage.getFilm(id) != null && userStorage.getUser(userId) != null) {
            filmStorage.removeLike(id, userId);
        }
    }

    public Film addFilm(FilmRequestDto dto) {
        return filmStorage.addFilm(dto);
    }

    public Film updateFilm(FilmRequestDto dto) {
        return filmStorage.updateFilm(dto);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }

    public void delete() {
        filmStorage.deleteAllFilms();
        userStorage.deleteAllUsers();
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted((p0, p1) ->
                        p1.getLikes().size() - p0.getLikes().size()
                )
                .limit(count)
                .collect(Collectors.toList());
    }
}
