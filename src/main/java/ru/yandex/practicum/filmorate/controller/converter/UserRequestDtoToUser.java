package ru.yandex.practicum.filmorate.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.converter.exceptions.DtoException;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class UserRequestDtoToUser implements Converter<UserRequestDto, User> {

    @Override
    public User convert(UserRequestDto dto) {
        if (dto==null) {
            throw new DtoException("пустой dto не подлежит конвертации в объект Film");
        }
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());
        return user;
    }
}