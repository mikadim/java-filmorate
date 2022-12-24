package ru.yandex.practicum.filmorate.exception;

public class UserStorageError extends RuntimeException {
    public UserStorageError(String message) {
        super(message);
    }
}
