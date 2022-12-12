package ru.yandex.practicum.filmorate.utils;

import org.springframework.stereotype.Component;

@Component()
public class FilmIdGenerator {
    private int id = 0;

    public int getId() {
        return ++id;
    }

    public void setForTest() {
        id = 0;
    }
}
