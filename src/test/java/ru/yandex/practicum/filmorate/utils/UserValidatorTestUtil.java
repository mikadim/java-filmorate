package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;

import java.time.LocalDate;

public class UserValidatorTestUtil {
    public static UserRequestDto getUserRequestDto(String email, String login, String name, LocalDate birthday) {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail(email);
        userRequestDto.setLogin(login);
        userRequestDto.setName(name);
        userRequestDto.setBirthday(birthday);
        return userRequestDto;
    }
}
