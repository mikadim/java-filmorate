package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;

@Service
public class UserService {
    private final UserStorage storage;

    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public Set<Integer> getFriends(Integer id) {
        return storage.getUser(id).getFriends();
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
}
