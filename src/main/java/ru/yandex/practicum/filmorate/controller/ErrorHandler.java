package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.Map;

@ControllerAdvice(assignableTypes = {UserController.class, FilmController.class, CommonDataController.class})
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @ExceptionHandler
    public ResponseEntity<String> handleConverterException(final DtoException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>("Проверьте тело запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilmStorageException.class)
    public ResponseEntity<Map<String, String>> handleFilmStorage(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(Map.of("Ошибка в хранилище фильмов - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserStorageException.class)
    public ResponseEntity<Map<String, String>> handleUserStorage(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(Map.of("Ошибка в хранилище пользователей - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MpaStorageException.class)
    public ResponseEntity<Map<String, String>> handleMapStorage(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(Map.of("Ошибка в хранилище данных MPA - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GenreStorageException.class)
    public ResponseEntity<Map<String, String>> handleGenreStorage(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(Map.of("Ошибка в хранилище жанров - ", e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
