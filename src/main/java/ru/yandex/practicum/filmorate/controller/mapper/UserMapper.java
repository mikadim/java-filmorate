package ru.yandex.practicum.filmorate.controller.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.model.User;

@Mapper
public interface UserMapper {
    User mapToUser(UserRequestDto dto);
}
