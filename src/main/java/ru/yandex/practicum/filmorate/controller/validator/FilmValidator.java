package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Optional;

public class FilmValidator implements ConstraintValidator<FilmValid, FilmRequestDto> {

    public static final int MAX_DESCRIPTION_SIZE = 200;

    @Override
    public boolean isValid(FilmRequestDto filmRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        if (Optional.of(filmRequestDto.getDescription()).isPresent()
                && filmRequestDto.getDescription().length() > MAX_DESCRIPTION_SIZE) {
            return false;
        }
        if (Optional.of(filmRequestDto.getReleaseDate()).isPresent()
                && filmRequestDto.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            return false;
        }

        return true;
    }
}
