package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class UserValidator implements ConstraintValidator<UserValid, UserRequestDto> {

    @Override
    public boolean isValid(UserRequestDto userRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        if (userRequestDto.getBirthday().isAfter(LocalDate.now()) || userRequestDto.getLogin().contains(" ")) {
            return false;
        }

        if (userRequestDto.getName() == null || userRequestDto.getName().isBlank() || userRequestDto.getName().isEmpty()) {
            userRequestDto.setName(userRequestDto.getLogin());
        }
        return true;
    }
}
