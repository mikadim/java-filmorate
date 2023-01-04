package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.FilmStorageException;
import ru.yandex.practicum.filmorate.exception.UserStorageException;
import ru.yandex.practicum.filmorate.utils.FilmIdGenerator;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> films = new ArrayList<>();
    private final ConversionService conversionService;
    private final FilmIdGenerator idGenerator;

    public InMemoryFilmStorage(ConversionService conversionService, FilmIdGenerator idGenerator) {
        this.conversionService = conversionService;
        this.idGenerator = idGenerator;
    }

    @Override
    public List<Film> getFilms() {
        return films;
    }

    @Override
    public Film addFilm(FilmRequestDto dto) {
        dto.setId(idGenerator.getId());
        Film film = conversionService.convert(dto, Film.class);
        films.add(film);
        return film;
    }

    @Override
    public Film updateFilm(FilmRequestDto dto) {
        Film film = conversionService.convert(dto, Film.class);
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                return film;
            }
        }
        throw new FilmStorageException("Фильм для обновления не найден");
    }

    @Override
    public void deleteFilm(Integer id) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == id) {
                films.remove(i);
            }
        }
        throw new FilmStorageException("Фильм для удаления не найден");
    }

    @Override
    public Film getFilm(Integer id) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == id) {
                return films.get(i);
            }
        }
        throw new UserStorageException("Фильм не найден");
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        getFilm(id).setLike(userId);
    }

    @Override
    public void removeLike(Integer id, Integer userId) {
        getFilm(id).getLikes().remove(userId);
    }
}
