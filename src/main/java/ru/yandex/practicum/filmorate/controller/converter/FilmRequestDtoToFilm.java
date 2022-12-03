package ru.yandex.practicum.filmorate.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class FilmRequestDtoToFilm implements Converter<FilmRequestDto, Film> {

    @Override
    public Film convert(FilmRequestDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        return film;
    }
}
