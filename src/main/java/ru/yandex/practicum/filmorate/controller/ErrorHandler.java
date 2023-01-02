package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.Map;

@ControllerAdvice(assignableTypes = {UserController.class, FilmController.class, CommonDataController.class})
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleConverterException(final DtoException e) {
        return new ResponseEntity<>("Проверьте тело запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilmStorageError.class)
    public ResponseEntity<Map<String, String>> handleFilmStorage(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("Ошибка в хранилище фильмов - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserStorageError.class)
    public ResponseEntity<Map<String, String>> handleUserStorage(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("Ошибка в хранилище пользователей - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MpaStorageError.class)
    public ResponseEntity<Map<String, String>> handleMapStorage(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("Ошибка в хранилище данных MPA - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GenreStorageError.class)
    public ResponseEntity<Map<String, String>> handleGenreStorage(final RuntimeException e) {
        return new ResponseEntity<>(Map.of("Ошибка в хранилище жанров - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
