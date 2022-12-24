package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.DtoException;
import ru.yandex.practicum.filmorate.exception.FilmStrorageError;
import ru.yandex.practicum.filmorate.exception.UserStorageError;

import java.util.Map;

@ControllerAdvice(assignableTypes = {UserController.class, FilmController.class})
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleConverterException(final DtoException e) {
        return new ResponseEntity<>("Проверьте тело запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilmStrorageError.class)
    public ResponseEntity<Map<String, String>> handleFilmStorage(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("Ошибка в хранилище фильмов - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserStorageError.class)
    public ResponseEntity<Map<String, String>> handleUserStorage(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("Ошибка в хранилище пользователей - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

}
