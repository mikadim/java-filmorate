package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.FilmStrorageError;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage storage;

    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public Set<Integer> getFriends(Integer id) {
        return storage.getUser(id).getFriends();
    }

    public List<User> getUserFriends(Integer id) {
        User user = storage.getUser(id);
        return user.getFriends().stream()
                .map(storage::getUser)
                .collect(Collectors.toList());
    }

    public void addFriends(Integer id, Integer friendId) {
        User user = storage.getUser(id);
        User friend = storage.getUser(friendId);
        user.setFriend(friendId);
        friend.setFriend(id);
    }

    public void deleteFriends(Integer id, Integer friendId) {
        User user = storage.getUser(id);
        User friend = storage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public User addUser(UserRequestDto dto) {
        return storage.addUser(dto);
    }

    public User updateUser(UserRequestDto dto) {
        return storage.updateUser(dto);
    }

    public List<User> getUsers() {
        return storage.getUsers();
    }

    public User getUser(Integer id) {
        return storage.getUser(id);
    }

    public void deleteUser(Integer id) {
        storage.deleteUser(id);
    }

    public void delete() {
        List<Integer> collect = storage.getUsers().stream().map(user -> user.getId()).collect(Collectors.toList());
        for (Integer id : collect) {
            try {
                storage.deleteUser(id);
            } catch (FilmStrorageError e) {
                log.debug("Пользователь с id={} не найден", id);
            }
        }
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        Set<Integer> otherFiends = storage.getUser(otherId).getFriends();
        User user = storage.getUser(id);
        return user.getFriends().stream()
                .filter(otherFiends::contains)
                .map(storage::getUser)
                .collect(Collectors.toList());
    }
}
