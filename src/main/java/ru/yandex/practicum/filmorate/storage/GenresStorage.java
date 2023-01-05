package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreStorageException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenresStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenre(Integer id) {
        String query = "select * from GENRES where ID = ?";
        List<Genre> list = jdbcTemplate.query(query, this::mapRowToGenre, id);
        if (list.size() > 0) {
            return list.get(0);
        }
        throw new GenreStorageException("Жанр не найден");
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("ID"));
        genre.setName(resultSet.getString("NAME"));
        return genre;
    }

    public List<Genre> getAllGenre() {
        String query = "select * from GENRES";
        return jdbcTemplate.query(query, this::mapRowToGenre);
    }
}
