package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.utils.UserIdGenerator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.utils.TestControllerUtils.readRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    public final static String USER_URL_PATH = "/users";
    public final static String USER_URL_PATH_TEST = "/users/clear-for-test";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc.perform(delete(USER_URL_PATH_TEST));
        applicationContext.getBean(UserIdGenerator.class).setForTest();
    }

    @Test
    @DisplayName("1. Корректный PUT проходит без ошибок")
    void sendCorrectPut() throws Exception {
        mockMvc.perform(
                post(USER_URL_PATH)
                        .content(readRequest("json", "user.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put(USER_URL_PATH)
                                .content(readRequest("json", "update_user.json"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("2. Корректный POST проходит без ошибок")
    void sendCorrectPost() throws Exception {
        mockMvc.perform(
                post(USER_URL_PATH)
                        .content(readRequest("json", "user.json"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("3. Некорректный POST возвращает 400")
    void sendIncorrectPost() throws Exception {
        mockMvc.perform(
                post(USER_URL_PATH)
                        .content(readRequest("json", "bad_user.json"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(400));
    }

    @Test
    @DisplayName("4. Не корректный PUT возвращает 400")
    void sendIncorrectPut() throws Exception {
        mockMvc.perform(
                put(USER_URL_PATH)
                        .content(readRequest("json", "bad_update_user.json"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(400));
    }

    @Test
    @DisplayName("5. Get корректно возвращает данные")
    void sendCorrectGet() throws Exception {
        mockMvc.perform(
                post(USER_URL_PATH)
                        .content(readRequest("json", "user.json"))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        get(USER_URL_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("dolore"))
                .andExpect(jsonPath("$[0].name").value("Nick Name"))
                .andExpect(jsonPath("$[0].email").value("mail@mail.ru"))
                .andExpect(jsonPath("$[0].birthday").value("1946-08-20"));
    }
}
