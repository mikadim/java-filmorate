package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage storage;

    public UserService(@Qualifier("dbStorage") UserStorage storage) {
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
        storage.addFriend(id, friendId);
    }

    public void deleteFriends(Integer id, Integer friendId) {
        storage.removeFriend(id, friendId);
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
        storage.deleteAllUsers();
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
