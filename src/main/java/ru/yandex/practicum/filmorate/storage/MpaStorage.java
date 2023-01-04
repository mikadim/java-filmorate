package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaStorageException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpa(Integer id) {
        String query = "select * from MPA where ID = ?";
        List<Mpa> list = jdbcTemplate.query(query, this::mapRowToMpa, id);
        if (list.size() > 0) {
            return list.get(0);
        }
        throw new MpaStorageException("MPA не найден");
    }

    public List<Mpa> getAllMpa() {
        String query = "select * from MPA";
        return jdbcTemplate.query(query, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("ID"));
        mpa.setName(resultSet.getString("NAME"));
        return mpa;
    }
}
