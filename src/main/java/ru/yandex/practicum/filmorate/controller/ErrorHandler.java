package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.controller.converter.exceptions.DtoException;

@ControllerAdvice(assignableTypes = {UserController.class, FilmController.class})

public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleConverterException(final DtoException e) {
        return new ResponseEntity<>("проверьте тело запроса", HttpStatus.BAD_REQUEST);
    }

}
