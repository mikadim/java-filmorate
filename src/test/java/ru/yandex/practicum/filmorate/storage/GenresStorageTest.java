package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.GenreStorageException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenresStorageTest {
    private final GenresStorage storage;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("1. Правильное название жанра для корректного id")
    public void getGenreNameForCorrectId() {
        String name = assertDoesNotThrow(() -> storage.getGenre(3)).getName();
        assertEquals("Мультфильм", name);
    }

    @Test
    @DisplayName("2. Исключение для некорректного id")
    public void getGenreForIncorrectId() {
        String message = assertThrows(GenreStorageException.class, () -> storage.getGenre(99)).getMessage();
        assertEquals("Жанр не найден", message);
    }

    @Test
    @DisplayName("3. Список из всех строк при запросе всех жанров")
    public void getAllGenre() {
        Integer rowNumber = jdbcTemplate.queryForObject("select count(*) from GENRES", Integer.class);
        assertEquals(storage.getAllGenre().size(), rowNumber);
    }
}
