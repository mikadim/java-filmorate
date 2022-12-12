package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.utils.FilmIdGenerator;
import ru.yandex.practicum.filmorate.utils.UserIdGenerator;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.utils.TestControllerUtils.readRequest;
import static ru.yandex.practicum.filmorate.controller.UserControllerTest.USER_URL_PATH;
import static ru.yandex.practicum.filmorate.controller.UserControllerTest.USER_URL_PATH_TEST;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmControllerTest {
    public final static String FILM_URL_PATH = "/films";
    public final static String FILM_URL_PATH_TEST = "/films/clear-for-test";

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc.perform(delete(FILM_URL_PATH_TEST));
        applicationContext.getBean(FilmIdGenerator.class).setForTest();
        mockMvc.perform(delete(USER_URL_PATH_TEST));
        applicationContext.getBean(UserIdGenerator.class).setForTest();
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("1. Корректный PUT проходит без ошибок")
    void sendCorrectPut() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                put(FILM_URL_PATH)
                        .content(readRequest("json", "update_film.json"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName("2. Корректный POST проходит без ошибок")
    void sendCorrectPost() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film.json"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("3. Некорректный POST возвращает 400")
    void sendIncorrectPost() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "bad_film.json"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(400));
    }

    @Test
    @Order(4)
    @DisplayName("4. Некорректный PUT возвращает 404")
    void sendIncorrectPut() throws Exception {
        mockMvc.perform(
                put(FILM_URL_PATH)
                        .content(readRequest("json", "update_film.json"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(404));
    }

    @Test
    @Order(5)
    @DisplayName("5. Get корректно возвращает данные")
    void sendCorrectGet() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        get(FILM_URL_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("billy bill"))
                .andExpect(jsonPath("$[0].description").value("kil bill"))
                .andExpect(jsonPath("$[0].releaseDate").value("1967-03-25"))
                .andExpect(jsonPath("$[0].duration").value(100));
    }

    @Test
    @Order(6)
    @DisplayName("6. Корректный Get по id возвращает данные")
    void sendCorrectGetId() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        get(FILM_URL_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("billy bill"))
                .andExpect(jsonPath("description").value("kil bill"))
                .andExpect(jsonPath("releaseDate").value("1967-03-25"))
                .andExpect(jsonPath("duration").value(100));
    }

    @Test
    @Order(7)
    @DisplayName("7. Некорректный Get по id возвращает ошибку")
    void sendIncorrectGetId() throws Exception {
        mockMvc.perform(
                        get(FILM_URL_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @DisplayName("8. Лайк с корректным пользователем проходит без ошибок")
    void sendCorrectLikePut() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                post(USER_URL_PATH)
                        .content(readRequest("json", "user.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put(FILM_URL_PATH + "/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    @DisplayName("9. Лайк с некорректным пользователем возвращает ошибку")
    void sendIncorrectLikePut() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put(FILM_URL_PATH + "/1/like/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("Пользователь не найден"));
    }

    @Test
    @Order(10)
    @DisplayName("10. Удаление лайка корректного пользователя проходит без ошибок")
    void sendCorrectLikeDelete() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film_with_like.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                post(USER_URL_PATH)
                        .content(readRequest("json", "user.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        delete(FILM_URL_PATH + "/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    @DisplayName("11. Удаление лайка некорректного пользователя возвращает ошибку")
    void sendIncorrectLikeDelete() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film_with_like.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        delete(FILM_URL_PATH + "/1/like/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("Пользователь не найден"));
    }

    @Test
    @Order(12)
    @DisplayName("12. Get самого популярного фильма возвращает корретный фильм")
    void getPopularFilm() throws Exception {
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film_with_like.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film_with_many_likes.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                post(FILM_URL_PATH)
                        .content(readRequest("json", "film.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        get(FILM_URL_PATH + "/popular?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("best"));
    }
}
