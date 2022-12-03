package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.utils.UserValidatorTestUtil.getUserRequestDto;

public class UserValidatorTest {
    UserRequestDto userRequestDto;
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void beforeEach() {
        userRequestDto = getUserRequestDto("user@yandex.ru", "login", "user",
                LocalDate.of(2022, 1, 1));
    }

    @Test
    @DisplayName("1. Юзер с корректными аргументами прохдит валидацию")
    void createUserRequestDtoWithCorrectArguments() {
        Assertions.assertTrue(validator.validate(userRequestDto).isEmpty());
    }

    @Test
    @DisplayName("2. Юзер с пустым логином не прохдит валидацию")
    void createUserRequestDtoWithEmptyLogin() {
        userRequestDto.setLogin("");
        Assertions.assertFalse(validator.validate(userRequestDto).isEmpty());
    }

    @Test
    @DisplayName("3. Юзер с пробелами в логине не прохдит валидацию")
    void createUserRequestDtoWithSpaceInLogin() {
        userRequestDto.setLogin("log in");
        Assertions.assertFalse(validator.validate(userRequestDto).isEmpty());
    }

    @Test
    @DisplayName("4. Юзер c пустым именем прохдит валидацию и логин прописывается в имя")
    void createUserRequestDtoWithEmptyName() {
        userRequestDto.setName("");
        Assertions.assertTrue(validator.validate(userRequestDto).isEmpty());
        Assertions.assertEquals(userRequestDto.getLogin(), userRequestDto.getName());
    }

    @Test
    @DisplayName("5. Юзер c null именем прохдит валидацию и логин прописывается в имя")
    void createUserRequestDtoWithNullName() {
        userRequestDto.setName(null);
        Assertions.assertTrue(validator.validate(userRequestDto).isEmpty());
        Assertions.assertEquals(userRequestDto.getLogin(), userRequestDto.getName());
    }

    @Test
    @DisplayName("6. Юзер c пробелами в имени прохдит валидацию и логин прописывается в имя")
    void createUserRequestDtoWithSpaceName() {
        userRequestDto.setName("   ");
        Assertions.assertTrue(validator.validate(userRequestDto).isEmpty());
        Assertions.assertEquals(userRequestDto.getLogin(), userRequestDto.getName());
    }

    @Test
    @DisplayName("7. Юзер c null почтой не прохдит валидацию")
    void createUserRequestDtoWithNullMail() {
        userRequestDto.setEmail(null);
        Assertions.assertFalse(validator.validate(userRequestDto).isEmpty());
    }

    @Test
    @DisplayName("8. Юзер c пустой почтой не прохдит валидацию")
    void createUserRequestDtoWithEmptyMail() {
        userRequestDto.setEmail("");
        Assertions.assertFalse(validator.validate(userRequestDto).isEmpty());
    }

    @Test
    @DisplayName("9. Юзер с почтой без символа @ не прохдит валидацию")
    void createUserRequestDtoWithoutAmpersandInMail() {
        userRequestDto.setEmail("useryandex.ru");
        Assertions.assertFalse(validator.validate(userRequestDto).isEmpty());
    }

    @Test
    @DisplayName("10. Юзер с датой рождения равной сегодняшнему числу прохдит валидацию")
    void createUserRequestDtoWithNowBirthDate() {
        userRequestDto.setBirthday(LocalDate.now());
        Assertions.assertTrue(validator.validate(userRequestDto).isEmpty());
    }

    @Test
    @DisplayName("11. Юзер с датой рождения равной сегодняшнему числу прохдит валидацию")
    void createUserRequestDtoWithFutureBirthDate() {
        userRequestDto.setBirthday(LocalDate.now().plusDays(1));
        Assertions.assertFalse(validator.validate(userRequestDto).isEmpty());
    }
}
