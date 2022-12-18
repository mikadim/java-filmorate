package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer id, Integer userId) {
        Film film = filmStorage.getFilm(id);
        if (userStorage.getUser(userId) != null) {
            film.setLike(id);
        }
    }

    public void deleteLike(Integer id, Integer userId) {
        Film film = filmStorage.getFilm(id);
        if (userStorage.getUser(userId) != null) {
            film.getLikes().remove(userId);
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

}
