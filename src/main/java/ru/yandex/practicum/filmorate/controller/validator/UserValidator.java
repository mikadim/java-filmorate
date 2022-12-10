package ru.yandex.practicum.filmorate.controller.validator;

import org.apache.commons.lang3.StringUtils;
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

        if (StringUtils.isBlank(userRequestDto.getName())) {
            userRequestDto.setName(userRequestDto.getLogin());
        }
        return true;
    }
}
