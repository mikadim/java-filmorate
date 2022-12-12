package ru.yandex.practicum.filmorate.storage.user.exception;

public class UserStorageError extends RuntimeException {
    public UserStorageError(String message) {
        super(message);
    }
}
