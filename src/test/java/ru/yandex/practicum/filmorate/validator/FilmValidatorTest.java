package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.controller.dto.MpaDto;

import javax.validation.Validation;
import javax.validation.Validator;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.utils.FilmValidatorTestUtil.getFilmRequestDto;

public class FilmValidatorTest {
    private FilmRequestDto filmRequestDto;
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void beforeEach() {
        filmRequestDto = getFilmRequestDto("Avatar", "012345678901234567890123456789012345678901234567" +
                        "89012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789",
                LocalDate.of(1895, 12, 28), 1, new MpaDto(1));
    }

    @Test
    @DisplayName("1. Фильм с корректными аргументами прохдит валидацию")
    void createFilmRequestDtoWithCorrectArguments() {
        Assertions.assertTrue(validator.validate(filmRequestDto).isEmpty());
    }

    @ParameterizedTest(name = "{index}. Имя = {arguments} ")
    @ValueSource(strings = {"", " "})
    @DisplayName("2. Фильм с пустым названием не прохдит валидацию")
    void createFilmRequestDtoWithIncorrectName(String name) {
        filmRequestDto.setName(name);
        Assertions.assertFalse(validator.validate(filmRequestDto).isEmpty());
    }

    @ParameterizedTest(name = "{index}. Продолжительность = {arguments} ")
    @ValueSource(ints = {-1, 0})
    @DisplayName("3. Фильм с не положительной продолжительностью не прохдит валидацию")
    void createFilmRequestDtoWithIncorrectDuration(int duration) {
        filmRequestDto.setDuration(duration);
        Assertions.assertFalse(validator.validate(filmRequestDto).isEmpty());
    }

    @Test
    @DisplayName("4. Фильм с релизом до 28.12.1895 не прохдит валидацию")
    void createFilmRequestDtoWithIncorrectReleaseDate() {
        filmRequestDto.setReleaseDate(LocalDate.of(1895, 12, 27));
        Assertions.assertFalse(validator.validate(filmRequestDto).isEmpty());
    }

    @Test
    @DisplayName("5. Фильм с описанием более 200 символов не прохдит валидацию")
    void createFilmRequestDtoWithIncorrectDescription() {
        filmRequestDto.setDescription("01234567890123456789012345678901234567890123456789012345678901234567890123456" +
                "789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345" +
                "6789012345678901234567890");
        Assertions.assertFalse(validator.validate(filmRequestDto).isEmpty());
    }


}
