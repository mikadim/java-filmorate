package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.exception.FilmStrorageError;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

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

    public void delete() {
        List<Integer> collect = filmStorage.getFilms().stream().map(Film::getId).collect(Collectors.toList());
        for (Integer id : collect) {
            try {
                filmStorage.deleteFilm(id);
            } catch (FilmStrorageError e) {
                log.debug("Фильм с id={} не найден", id);
            }
        }
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
