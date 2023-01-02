package ru.yandex.practicum.filmorate.exception;

public class GenreStorageError extends RuntimeException {
    public GenreStorageError(String message) {
        super(message);
    }
}
