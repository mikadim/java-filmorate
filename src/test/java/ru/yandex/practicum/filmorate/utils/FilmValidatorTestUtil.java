package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;

import java.time.LocalDate;

public class FilmValidatorTestUtil {
    public static FilmRequestDto getFilmRequestDto(String name, String description, LocalDate releaseDate, int duration) {
        FilmRequestDto filmRequestDto = new FilmRequestDto();
        filmRequestDto.setName(name);
        filmRequestDto.setDescription(description);
        filmRequestDto.setReleaseDate(releaseDate);
        filmRequestDto.setDuration(duration);
        return filmRequestDto;
    }
}
