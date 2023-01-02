package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(UserRequestDto dto);

    User updateUser(UserRequestDto dto);

    void deleteUser(Integer id);

    List<User> getUsers();

    User getUser(Integer id);

    void addFriend(Integer id, Integer friendId);

    void removeFriend(Integer id, Integer friendId);

    void deleteAllUsers();
}
