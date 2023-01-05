package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.exception.UserStorageException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final UserDbStorage storage;
    private User testFriend;
    private User testUser;
    private UserRequestDto testUserDto;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        storage.deleteAllUsers();
        UserRequestDto testFriendDto = new UserRequestDto();
        testFriendDto.setLogin("Friend_Login");
        testFriendDto.setName("Friend_Name");
        testFriendDto.setEmail("friend@mail.com");
        testFriendDto.setBirthday(LocalDate.of(2000, 1, 1));
        testFriend = storage.addUser(testFriendDto);
        testUserDto = new UserRequestDto();
        testUserDto.setLogin("User_Login");
        testUserDto.setName("User_Name");
        testUserDto.setEmail("user@mail.com");
        testUserDto.setBirthday(LocalDate.of(2010, 10, 10));
        testUser = storage.addUser(testUserDto);
    }

    @Test
    @DisplayName("1. Создание пользователя с корректными данными")
    public void addUserWithCorrectData() {
        UserRequestDto newUserDto = new UserRequestDto();
        newUserDto.setLogin("NewUser_Login");
        newUserDto.setName("NewUser_Name");
        newUserDto.setEmail("newuser@mail.com");
        newUserDto.setBirthday(LocalDate.of(2000, 1, 1));
        User user = assertDoesNotThrow(() -> storage.addUser(newUserDto));
        assertThat(user).matches((u) -> u.getLogin().equals("NewUser_Login")
                && u.getName().equals("NewUser_Name")
                && u.getEmail().equals("newuser@mail.com")
                && u.getBirthday().equals(LocalDate.of(2000, 1, 1)));
    }

    @Test
    @DisplayName("2. Обновление существующего пользователя")
    public void updateUserWhichExist() {
        UserRequestDto newUserDto = new UserRequestDto();
        newUserDto.setId(testUser.getId());
        newUserDto.setLogin("Update_Login");
        newUserDto.setName("Update_Name");
        newUserDto.setEmail("update@mail.com");
        newUserDto.setBirthday(LocalDate.of(2012, 12, 12));
        User updatedUser = assertDoesNotThrow(() -> storage.addUser(newUserDto));
        assertThat(updatedUser).matches((u) -> u.getLogin().equals("Update_Login")
                && u.getName().equals("Update_Name")
                && u.getEmail().equals("update@mail.com")
                && u.getBirthday().equals(LocalDate.of(2012, 12, 12)));
    }

    @Test
    @DisplayName("3. Исключение для несуществующего пользователя")
    public void updateUserWhichNotExist() {
        testUserDto.setId(0);
        String message = assertThrows(UserStorageException.class, () -> storage.updateUser(testUserDto)).getMessage();
        assertEquals("Пользователь для обновления не найден", message);
    }

    @Test
    @DisplayName("4. Удаление существующего/не существующего пользователя")
    public void deleteUsers() {
        Integer userId = testUser.getId();
        assertDoesNotThrow(() -> storage.getUser(userId));
        storage.deleteUser(userId);
        String message = assertThrows(UserStorageException.class, () -> storage.getUser(userId)).getMessage();
        assertEquals("Пользователь не найден", message);
    }

    @Test
    @DisplayName("5. В список всех пользователей попадают все строки")
    public void getAllUsers() {
        Integer rowNumber = jdbcTemplate.queryForObject("select count(*) from USERS", Integer.class);
        assertEquals(storage.getUsers().size(), rowNumber);
    }

    @Test
    @DisplayName("6. Получение существующего пользователя")
    public void getUserWithCorrectId() {
        Integer userId = testUser.getId();
        User user = assertDoesNotThrow(() -> storage.getUser(userId));
        assertThat(user).matches((u) -> u.getLogin().equals("User_Login")
                && u.getName().equals("User_Name")
                && u.getEmail().equals("user@mail.com")
                && u.getBirthday().equals(LocalDate.of(2010, 10, 10)));
    }

    @Test
    @DisplayName("7. Исключение при получении несуществующего пользователя")
    public void getUserWithIncorrectId() {
        String message = assertThrows(UserStorageException.class, () -> storage.getUser(99999)).getMessage();
        assertEquals("Пользователь не найден", message);
    }

    @Test
    @DisplayName("8. Добавление существующего пользователя в друзья")
    public void addFriendWithCorrectUsers() {
        Integer userId = testUser.getId();
        Integer friendId = testFriend.getId();
        assertDoesNotThrow(() -> storage.addFriend(userId, friendId));
        assertThat(storage.getUser(userId)).matches((u) -> u.getFriends().contains(friendId)
                && u.getFriends().size() == 1);
        assertDoesNotThrow(() -> storage.addFriend(userId, friendId));
        assertThat(storage.getUser(userId)).matches((u) -> u.getFriends().size() == 1);
    }

    @Test
    @DisplayName("9. Исключение при добавлении в друзья несуществующих пользователей")
    public void addFriendWithIncorrectUsers() {
        Integer userId = testUser.getId();
        String message = assertThrows(UserStorageException.class, () -> storage.addFriend(userId, 99999)).getMessage();
        assertEquals("Пользователь не найден", message);
        assertThat(storage.getUser(userId)).matches((u) -> u.getFriends().isEmpty());
    }

    @Test
    @DisplayName("10. Удаление из друзей пользователей")
    public void removeFriend() {
        Integer userId = testUser.getId();
        Integer friendId = testFriend.getId();
        assertDoesNotThrow(() -> {
            storage.removeFriend(userId, friendId);
            storage.removeFriend(userId, 9999);
        });
        storage.addFriend(userId, friendId);
        storage.removeFriend(userId, friendId);
        assertThat(storage.getUser(userId)).matches((u) -> u.getFriends().isEmpty());
    }
}
