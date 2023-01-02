package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(FilmRequestDto dto);

    Film updateFilm(FilmRequestDto dto);

    void deleteFilm(Integer id);

    List<Film> getFilms();

    Film getFilm(Integer id);

    void addLike(Integer id, Integer userId);

    void removeLike(Integer id, Integer userId);

    void deleteAllFilms();
}
