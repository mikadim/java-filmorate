package ru.yandex.practicum.filmorate.utils;

import org.springframework.stereotype.Component;

@Component
public class UserIdGenerator {
    private int id = 0;

    public int getId() {
        return ++id;
    }
}
