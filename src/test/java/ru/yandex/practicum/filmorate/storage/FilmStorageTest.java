package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.controller.dto.GenreDto;
import ru.yandex.practicum.filmorate.controller.dto.MpaDto;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.exception.FilmStorageException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private Film testFilm;
    FilmRequestDto testFilmDto;
    private User testUser;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        filmStorage.deleteAllFilms();
        userStorage.deleteAllUsers();
        UserRequestDto testUserDto = new UserRequestDto();
        testUserDto.setLogin("User_Login");
        testUserDto.setName("User_Name");
        testUserDto.setEmail("user@mail.com");
        testUserDto.setBirthday(LocalDate.of(2010, 10, 10));
        testUser = userStorage.addUser(testUserDto);
        testFilmDto = new FilmRequestDto();
        testFilmDto.setName("TestFilm_Name");
        testFilmDto.setDescription("TestFilm_Description");
        testFilmDto.setDuration(60);
        testFilmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilmDto.setMpa(new MpaDto(1));
        testFilmDto.setGenres(List.of(new GenreDto(1)));
        testFilm = filmStorage.addFilm(testFilmDto);
    }

    @Test
    @DisplayName("1. Создание фильма с корректными данными")
    public void addFilmWithCorrectData() {
        FilmRequestDto newFilmDto = new FilmRequestDto();
        newFilmDto.setName("NewFilm_Name");
        newFilmDto.setDescription("NewFilm_Description");
        newFilmDto.setDuration(60);
        newFilmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        newFilmDto.setLikes(Set.of(testUser.getId()));
        newFilmDto.setMpa(new MpaDto(4));
        newFilmDto.setGenres(List.of(new GenreDto(2)));
        Film film = assertDoesNotThrow(() -> filmStorage.addFilm(newFilmDto));
        assertThat(film).matches((f) -> f.getName().equals("NewFilm_Name")
                && f.getDescription().equals("NewFilm_Description")
                && f.getDuration() == 60
                && f.getReleaseDate().equals(LocalDate.of(2000, 1, 1))
                && f.getLikes().size() == 1 && f.getLikes().contains(testUser.getId())
                && f.getMpa().getId() == 4
                && f.getGenres().size() == 1 && f.getGenres().get(0).getId() == 2);
    }

    @Test
    @DisplayName("2. Обновление существующего фильма")
    public void updateFilmWhichExist() {
        FilmRequestDto newFilmDto = new FilmRequestDto();
        newFilmDto.setId(testFilm.getId());
        newFilmDto.setName("UpdateFilm_Name");
        newFilmDto.setDescription("UpdateFilm_Description");
        newFilmDto.setDuration(100);
        newFilmDto.setReleaseDate(LocalDate.of(2011, 11, 11));
        newFilmDto.setMpa(new MpaDto(4));
        newFilmDto.setGenres(List.of(new GenreDto(2)));
        Film film = assertDoesNotThrow(() -> filmStorage.updateFilm(newFilmDto));
        assertThat(film).matches((f) -> f.getName().equals("UpdateFilm_Name")
                && f.getDescription().equals("UpdateFilm_Description")
                && f.getDuration() == 100
                && f.getReleaseDate().equals(LocalDate.of(2011, 11, 11))
                && f.getMpa().getId() == 4
                && f.getGenres().size() == 1 && f.getGenres().get(0).getId() == 2);
    }

    @Test
    @DisplayName("3. Исключение для несуществующего фильма")
    public void updateFilmWhichNotExist() {
        testFilmDto.setId(0);
        String message = assertThrows(FilmStorageException.class, () -> filmStorage.updateFilm(testFilmDto)).getMessage();
        assertEquals("Фильм для обновления не найден", message);
    }

    @Test
    @DisplayName("4. Удаление существующего/не существующего фильма")
    public void deleteFilms() {
        Integer filmId = testFilm.getId();
        assertDoesNotThrow(() -> filmStorage.getFilm(filmId));
        filmStorage.deleteFilm(filmId);
        String message = assertThrows(FilmStorageException.class, () -> filmStorage.getFilm(filmId)).getMessage();
        assertEquals("Фильм не найден", message);
    }

    @Test
    @DisplayName("5. В список всех фильмов попадают все строки")
    public void getAllFilms() {
        Integer rowNumber = jdbcTemplate.queryForObject("select count(*) from FILMS", Integer.class);
        assertEquals(filmStorage.getFilms().size(), rowNumber);
    }

    @Test
    @DisplayName("6. Получение существующего фильма")
    public void getFilmWithCorrectId() {
        Integer filmId = testFilm.getId();
        Film film = assertDoesNotThrow(() -> filmStorage.getFilm(filmId));
        assertThat(film).matches((f) -> f.getName().equals("TestFilm_Name")
                && f.getDescription().equals("TestFilm_Description")
                && f.getDuration() == 60
                && f.getReleaseDate().equals(LocalDate.of(2000, 1, 1))
                && f.getMpa().getId() == 1
                && f.getGenres().size() == 1 && f.getGenres().get(0).getId() == 1);
    }

    @Test
    @DisplayName("7. Исключение при получении несуществующего фильма")
    public void getFilmWithIncorrectId() {
        String message = assertThrows(FilmStorageException.class, () -> filmStorage.getFilm(99999)).getMessage();
        assertEquals("Фильм не найден", message);
    }

    @Test
    @DisplayName("8. Добавление лайка от существующего пользователя")
    public void addLikeByCorrectUsers() {
        Integer filmId = testFilm.getId();
        Integer userId = testUser.getId();
        assertDoesNotThrow(() -> filmStorage.addLike(filmId, userId));
        assertThat(filmStorage.getFilm(filmId)).matches((u) -> u.getLikes().contains(userId)
                && u.getLikes().size() == 1);
        assertDoesNotThrow(() -> filmStorage.addLike(filmId, userId));
        assertThat(filmStorage.getFilm(userId)).matches((u) -> u.getLikes().size() == 1);
    }

    @Test
    @DisplayName("9. Исключение при добавлении лайка от несуществующего пользователя")
    public void addLikeByIncorrectUsers() {
        Integer filmId = testFilm.getId();
        String message = assertThrows(FilmStorageException.class, () -> filmStorage.addLike(filmId, 99999)).getMessage();
        assertEquals("Фильм или пользователь не найден", message);
        assertThat(filmStorage.getFilm(filmId)).matches((u) -> u.getLikes().isEmpty());
    }

    @Test
    @DisplayName("10. Удаление из друзей пользователей")
    public void removeFriend() {
        Integer filmId = testFilm.getId();
        Integer userId = testUser.getId();
        assertDoesNotThrow(() -> {
            filmStorage.removeLike(filmId, userId);
            filmStorage.removeLike(filmId, 9999);
        });
        filmStorage.addLike(filmId, userId);
        filmStorage.removeLike(filmId, userId);
        assertThat(filmStorage.getFilm(filmId)).matches((u) -> u.getLikes().isEmpty());
    }
}