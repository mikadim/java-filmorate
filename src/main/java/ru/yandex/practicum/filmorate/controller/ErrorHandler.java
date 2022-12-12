package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.controller.converter.exceptions.DtoException;
import ru.yandex.practicum.filmorate.storage.film.exception.FilmStrorageError;
import ru.yandex.practicum.filmorate.storage.user.exception.UserStorageError;

import java.util.Map;

@ControllerAdvice(assignableTypes = {UserController.class, FilmController.class})

public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleConverterException(final DtoException e) {
        return new ResponseEntity<>("проверьте тело запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({FilmStrorageError.class, UserStorageError.class})
    public ResponseEntity<Map<String, String>> handleStorages(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }

}
