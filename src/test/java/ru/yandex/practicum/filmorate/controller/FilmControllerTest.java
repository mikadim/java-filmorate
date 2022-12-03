package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.utils.TestControllerUtils.readRequest;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmControllerTest {
    public final static String FILM_URL_PATH = "/films";

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc.perform(delete(FILM_URL_PATH));
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
}
