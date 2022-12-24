package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.UserStorageError;
import ru.yandex.practicum.filmorate.utils.UserIdGenerator;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final List<User> users = new ArrayList<>();
    private final ConversionService conversionService;
    private final UserIdGenerator idGenerator;

    public InMemoryUserStorage(ConversionService conversionService, UserIdGenerator idGenerator) {
        this.conversionService = conversionService;
        this.idGenerator = idGenerator;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public User addUser(UserRequestDto dto) {
        dto.setId(idGenerator.getId());
        User user = conversionService.convert(dto, User.class);
        users.add(user);
        return user;
    }

    @Override
    public User updateUser(UserRequestDto dto) {
        User user = conversionService.convert(dto, User.class);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return user;
            }
        }
        throw new UserStorageError("Пользователь для обновления не найден");
    }

    @Override
    public void deleteUser(Integer id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                users.remove(i);
            }
        }
        throw new UserStorageError("Пользователь для удаления не найден");
    }

    @Override
    public User getUser(Integer id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                return users.get(i);
            }
        }
        throw new UserStorageError("Пользователь не найден");
    }
}