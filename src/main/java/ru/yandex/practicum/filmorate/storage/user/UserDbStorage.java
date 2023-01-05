package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.exception.UserStorageException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("dbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ConversionService conversionService;


    public UserDbStorage(JdbcTemplate jdbcTemplate, ConversionService conversionService) {
        this.jdbcTemplate = jdbcTemplate;
        this.conversionService = conversionService;
    }

    @Override
    public User addUser(UserRequestDto dto) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("ID");
        Integer id = insert.executeAndReturnKey(dto.toMap()).intValue();
        return getUser(id);
    }

    @Override
    public User updateUser(UserRequestDto dto) {
        String query = "update USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where ID = ?";
        int update = jdbcTemplate.update(query
                , dto.getEmail()
                , dto.getLogin()
                , dto.getName()
                , dto.getBirthday()
                , dto.getId());
        if (update > 0) {
            return getUser(dto.getId());
        }
        throw new UserStorageException("Пользователь для обновления не найден");
    }

    @Override
    public void deleteUser(Integer id) {
        String query = "delete from USERS where ID = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<User> getUsers() {
        String sql = "select u.*, (select listagg(id_user_2, ',') from FRIENDS where u.id = id_user_1) as friends " +
                "from USERS u";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User getUser(Integer id) {
        String query = "select u.*, (select listagg(id_user_2, ',') from FRIENDS where u.id = id_user_1) as friends " +
                "from USERS u where id = ?";
        List<User> list = jdbcTemplate.query(query, this::mapRowToUser, id);
        if (list.size() > 0) {
            return list.get(0);
        }
        throw new UserStorageException("Пользователь не найден");
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        String check = "select distinct ID from USERS where ID = ? OR ID = ? " +
                "union all " +
                "select ID from FRIENDS where ID_USER_1 = ? AND ID_USER_2  = ?";
        List<Integer> list = jdbcTemplate.query(check, (r, i) -> r.getInt("ID"), id, friendId, id, friendId);
        if (list.size() == 2) {
            String query = "insert into FRIENDS (ID_USER_1, ID_USER_2) values (?, ?)";
            jdbcTemplate.update(query, id, friendId);
        } else if (list.size() < 2) {
            throw new UserStorageException("Пользователь не найден");
        }
    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        String query = "delete from FRIENDS where ID_USER_1 = ? and ID_USER_2 = ?";
        jdbcTemplate.update(query, id, friendId);
    }

    @Override
    public void deleteAllUsers() {
        jdbcTemplate.update("delete from USERS");
        jdbcTemplate.execute("alter table USERS alter column ID RESTART WITH 1");
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("ID"));
        user.setEmail(resultSet.getString("EMAIL"));
        user.setLogin(resultSet.getString("LOGIN"));
        user.setName(resultSet.getString("NAME"));
        Optional.ofNullable(resultSet.getDate("BIRTHDAY")).ifPresent(s -> user.setBirthday(s.toLocalDate()));
        Optional.ofNullable(resultSet.getString("FRIENDS"))
                .ifPresent(s -> user.setFriends(Arrays.stream(s.split(","))
                        .map(Integer::valueOf)
                        .collect(Collectors.toSet())));
        return user;
    }
}
