package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.MpaStorageException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaStorageTest {
    private final MpaStorage storage;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("1. Правильное название MPA для корректного id")
    public void getMpaNameForCorrectId() {
        String name = assertDoesNotThrow(() -> storage.getMpa(3)).getName();
        assertEquals("PG-13", name);
    }

    @Test
    @DisplayName("2. Исключение для некорректного id")
    public void getMpaForIncorrectId() {
        String message = assertThrows(MpaStorageException.class, () -> storage.getMpa(99)).getMessage();
        assertEquals("MPA не найден", message);
    }

    @Test
    @DisplayName("3. Список из всех строк при запросе всех MPA")
    public void getAllMpa() {
        Integer rowNumber = jdbcTemplate.queryForObject("select count(*) from MPA", Integer.class);
        assertEquals(storage.getAllMpa().size(), rowNumber);
    }
}
