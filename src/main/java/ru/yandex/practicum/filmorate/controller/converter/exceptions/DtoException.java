package ru.yandex.practicum.filmorate.controller.converter.exceptions;

public class DtoException extends RuntimeException {
    public DtoException(String message) {
        super(message);
    }
}
