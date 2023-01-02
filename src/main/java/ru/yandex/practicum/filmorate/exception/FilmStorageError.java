package ru.yandex.practicum.filmorate.exception;

public class FilmStorageError extends RuntimeException {
    public FilmStorageError(String message) {
        super(message);
    }
}
